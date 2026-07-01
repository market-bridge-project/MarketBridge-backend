package com.example.marketbridgebe.domain.recommend.service;

import com.example.marketbridgebe.domain.recommend.RecommendKeyword;
import com.example.marketbridgebe.domain.recommend.client.NaverAiClient;
import com.example.marketbridgebe.domain.recommend.dto.RecommendRequestDto;
import com.example.marketbridgebe.domain.recommend.dto.RecommendResponseDto;
import com.example.marketbridgebe.domain.recommend.entity.StoreEmbedding;
import com.example.marketbridgebe.domain.recommend.repository.StoreEmbeddingRepository;
import com.example.marketbridgebe.domain.store.entity.Store;
import com.example.marketbridgebe.domain.store.repository.StoreRepository;
import com.example.marketbridgebe.global.exception.CustomException;
import com.example.marketbridgebe.global.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

	private static final int SHORT_ZONE_RANGE = 3;
	private static final int MEDIUM_ZONE_RANGE = 5;

	private final StoreRepository storeRepository;
	private final StoreEmbeddingRepository storeEmbeddingRepository;
	private final NaverAiClient naverAiClient;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final Map<Long, CachedEmbedding> storeEmbeddingCache = new ConcurrentHashMap<>();

	@Value("${naver.ai.max-store-embedding-candidates:100}")
	private int maxStoreEmbeddingCandidates;

	@Transactional
	public RecommendResponseDto recommendCourse(RecommendRequestDto request) {
		validate(request);

		List<Store> stores = storeRepository.findAll();
		if (stores.isEmpty()) {
			throw new CustomException(ErrorCode.STORE_NOT_FOUND);
		}

		String purpose = String.join(" ", request.who().trim(), request.what().trim(), request.time().trim());
		Optional<double[]> purposeEmbedding = naverAiClient.embed(purpose);
		List<ScoredStore> scoredStores = scoreStores(purpose, purposeEmbedding, stores);

		Store anchorStore = scoredStores.get(0).store();
		int targetCount = "길게".equals(request.time().trim()) ? 4 : 3;
		Integer maxZoneDistance = maxZoneDistance(request.time().trim());

		List<RecommendResponseDto.StoreDto> recommendedStores = scoredStores.stream()
			.filter(scored -> isInZoneRange(anchorStore, scored.store(), maxZoneDistance))
			.limit(targetCount)
			.sorted(Comparator.comparingInt((ScoredStore scored) -> zoneDistance(anchorStore, scored.store()))
				.thenComparing(Comparator.comparingDouble(ScoredStore::score).reversed())
				.thenComparing(scored -> scored.store().getId()))
			.map(scored -> RecommendResponseDto.StoreDto.from(scored.store()))
			.toList();

		return new RecommendResponseDto(buildCourseTitle(request), recommendedStores);
	}

	private List<ScoredStore> scoreStores(String purpose, Optional<double[]> purposeEmbedding, List<Store> stores) {
		List<ScoredStore> fallbackScoredStores = stores.stream()
			.map(store -> new ScoredStore(store, fallbackSimilarity(purpose, store)))
			.sorted(scoreComparator())
			.toList();

		if (purposeEmbedding.isEmpty()) {
			return fallbackScoredStores;
		}

		Set<Long> embeddingCandidateIds = fallbackScoredStores.stream()
			.limit(maxStoreEmbeddingCandidates)
			.map(scoredStore -> scoredStore.store().getId())
			.collect(Collectors.toSet());

		return fallbackScoredStores.stream()
			.map(scoredStore -> rescoreWithEmbedding(scoredStore, purposeEmbedding.get(), embeddingCandidateIds))
			.sorted(scoreComparator())
			.toList();
	}

	private ScoredStore rescoreWithEmbedding(ScoredStore fallbackScoredStore, double[] purposeEmbedding,
		Set<Long> embeddingCandidateIds) {
		Store store = fallbackScoredStore.store();
		if (!embeddingCandidateIds.contains(store.getId())) {
			return fallbackScoredStore;
		}

		return getStoreEmbedding(store)
			.filter(storeEmbedding -> purposeEmbedding.length == storeEmbedding.length)
			.map(storeEmbedding -> new ScoredStore(store, fallbackScoredStore.score()
				+ cosineSimilarity(purposeEmbedding, storeEmbedding) * 10))
			.orElse(fallbackScoredStore);
	}

	private Optional<double[]> getStoreEmbedding(Store store) {
		String storeText = toStoreText(store);
		CachedEmbedding cachedEmbedding = storeEmbeddingCache.get(store.getId());
		if (cachedEmbedding != null && cachedEmbedding.sourceText().equals(storeText)) {
			return Optional.of(cachedEmbedding.embedding());
		}

		Optional<double[]> savedEmbedding = getSavedStoreEmbedding(store.getId(), storeText);
		if (savedEmbedding.isPresent()) {
			storeEmbeddingCache.put(store.getId(), new CachedEmbedding(storeText, savedEmbedding.get()));
			return savedEmbedding;
		}

		Optional<double[]> fetchedEmbedding = naverAiClient.embed(storeText);
		fetchedEmbedding.ifPresent(embedding -> saveStoreEmbedding(store, storeText, embedding));
		return fetchedEmbedding;
	}

	private Optional<double[]> getSavedStoreEmbedding(Long storeId, String storeText) {
		return storeEmbeddingRepository.findByStoreId(storeId)
			.filter(storeEmbedding -> storeEmbedding.matchesSourceText(storeText))
			.flatMap(this::parseEmbedding);
	}

	private Optional<double[]> parseEmbedding(StoreEmbedding storeEmbedding) {
		try {
			return Optional.of(objectMapper.readValue(storeEmbedding.getEmbeddingJson(), double[].class));
		} catch (JsonProcessingException e) {
			log.warn("Stored embedding JSON parsing failed. storeEmbeddingId={}", storeEmbedding.getId(), e);
			return Optional.empty();
		}
	}

	private void saveStoreEmbedding(Store store, String storeText, double[] embedding) {
		try {
			String embeddingJson = objectMapper.writeValueAsString(embedding);
			StoreEmbedding storeEmbedding = storeEmbeddingRepository.findByStoreId(store.getId())
				.orElseGet(() -> StoreEmbedding.builder()
					.store(store)
					.sourceText(storeText)
					.embeddingJson(embeddingJson)
					.build());
			storeEmbedding.updateEmbedding(storeText, embeddingJson);
			storeEmbeddingRepository.save(storeEmbedding);
			storeEmbeddingCache.put(store.getId(), new CachedEmbedding(storeText, embedding));
		} catch (JsonProcessingException e) {
			log.warn("Store embedding JSON serialization failed. storeId={}", store.getId(), e);
		}
	}

	private Comparator<ScoredStore> scoreComparator() {
		return Comparator.comparingDouble(ScoredStore::score).reversed()
			.thenComparing(scored -> scored.store().getId());
	}

	private String buildCourseTitle(RecommendRequestDto request) {
		String who = request.who().trim();
		String what = request.what().trim();
		String titlePrefix = titlePrefix(who, request.time().trim());

		if (isMealPurpose(what)) {
			return titlePrefix + " 식사를 하는 코스";
		}
		if (isSnackPurpose(what)) {
			return titlePrefix + " 간식을 즐기는 코스";
		}
		if (isShoppingPurpose(what)) {
			return titlePrefix + " 장보는 코스";
		}
		if (isBrowsingPurpose(what)) {
			return titlePrefix + " 구경하는 코스";
		}

		return titlePrefix + " " + what + objectParticle(what) + " 즐기는 코스";
	}

	private String titlePrefix(String who, String time) {
		String titleDuration = titleDurationPhrase(time);
		if (!StringUtils.hasText(titleDuration)) {
			return who;
		}
		return who + " " + titleDuration;
	}

	private String titleDurationPhrase(String time) {
		if ("길게".equals(time)) {
			return "여유롭게";
		}
		if ("중간".equals(time)) {
			return "";
		}
		return time;
	}


	private String objectParticle(String word) {
		if (!StringUtils.hasText(word)) {
			return "";
		}

		char lastChar = word.charAt(word.length() - 1);
		if (lastChar < '가' || lastChar > '힣') {
			return "를";
		}

		return (lastChar - '가') % 28 == 0 ? "를" : "을";
	}

	private void validate(RecommendRequestDto request) {
		if (request == null
			|| !StringUtils.hasText(request.who())
			|| !StringUtils.hasText(request.what())
			|| !StringUtils.hasText(request.time())
			|| maxZoneDistance(request.time().trim()) == null && !"길게".equals(request.time().trim())) {
			throw new CustomException(ErrorCode.INVALID_REQUEST);
		}
	}

	private String toStoreText(Store store) {
		String keywordText = store.getKeywords().stream()
			.map(RecommendKeyword::getLabel)
			.reduce("", (left, right) -> left + " " + right);
		return String.join(" ",
			nullToEmpty(store.getName()),
			nullToEmpty(store.getCategory()),
			nullToEmpty(store.getIntro()),
			keywordText);
	}

	private double fallbackSimilarity(String purpose, Store store) {
		double score = purposeMatchScore(purpose, store);
		if (contains(purpose, store.getName())) score += 1;
		if (contains(purpose, store.getCategory())) score += 2;
		if (contains(purpose, store.getIntro())) score += 1;
		for (RecommendKeyword keyword : store.getKeywords()) {
			if (purpose.contains(keyword.getLabel()) || purpose.contains(keyword.name())) {
				score += 3;
			}
		}
		return score;
	}

	private double purposeMatchScore(String purpose, Store store) {
		if (isMealPurpose(purpose)) {
			return matchesMealStore(store) ? 80 : -80;
		}
		if (isSnackPurpose(purpose)) {
			return matchesSnackStore(store) ? 80 : -80;
		}
		if (isShoppingPurpose(purpose)) {
			return matchesShoppingStore(store) ? 80 : -80;
		}
		if (isBrowsingPurpose(purpose)) {
			return matchesBrowsingStore(store) ? 80 : -40;
		}
		return 0;
	}

	private boolean isMealPurpose(String purpose) {
		return purpose.contains(RecommendKeyword.MEAL.getLabel()) || purpose.contains(RecommendKeyword.MEAL.name());
	}

	private boolean isSnackPurpose(String purpose) {
		return purpose.contains(RecommendKeyword.SNACK.getLabel()) || purpose.contains(RecommendKeyword.SNACK.name());
	}

	private boolean isShoppingPurpose(String purpose) {
		return purpose.contains(RecommendKeyword.SHOPPING.getLabel())
			|| purpose.contains(RecommendKeyword.SHOPPING.name())
			|| purpose.contains("장보기")
			|| purpose.contains("장 보")
			|| purpose.contains("구매");
	}

	private boolean isBrowsingPurpose(String purpose) {
		return purpose.contains("구경") || purpose.contains("둘러보기") || purpose.contains("둘러");
	}

	private boolean matchesMealStore(Store store) {
		String text = toComparableText(store);
		return containsAny(text, "음식점", "식당", "맛집", "분식", "한식", "중식", "일식", "양식",
			"국밥", "찌개", "부대찌개", "칼국수", "밥", "국수", "떡볶이", "김밥", "족발", "치킨", "구이");
	}

	private boolean matchesSnackStore(Store store) {
		String text = toComparableText(store);
		return containsAny(text, "간식", "카페", "디저트", "분식", "떡", "빵", "베이커리", "도넛",
			"호떡", "튀김", "커피", "차", "아이스크림", "제과", "제빵", "분식집");
	}

	private boolean matchesShoppingStore(Store store) {
		String text = toComparableText(store);
		return containsAny(text, "농산물", "과일", "야채", "채소", "정육", "수산", "건어물", "반찬", "식료품",
			"마트", "잡화", "생활", "생활·서비스", "의류", "신발", "화장품", "상회", "청과",
			"꽃", "꽃방", "꽃집", "문구", "철물");
	}

	private boolean matchesBrowsingStore(Store store) {
		String text = toComparableText(store);
		return containsAny(text, "생활", "생활·서비스", "잡화", "의류", "신발", "화장품", "꽃", "꽃방",
			"꽃집", "문구", "소품", "공방", "상회", "청과", "농산물", "수산", "건어물", "시장");
	}

	private String toComparableText(Store store) {
		return String.join(" ",
			nullToEmpty(store.getName()),
			nullToEmpty(store.getCategory()),
			nullToEmpty(store.getIntro()));
	}

	private boolean containsAny(String text, String... keywords) {
		for (String keyword : keywords) {
			if (contains(text, keyword)) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(String purpose, String target) {
		return StringUtils.hasText(target) && purpose.contains(target);
	}

	private String nullToEmpty(String value) {
		return value == null ? "" : value;
	}

	private double cosineSimilarity(double[] a, double[] b) {
		double dot = 0;
		double aNorm = 0;
		double bNorm = 0;
		for (int i = 0; i < a.length; i++) {
			dot += a[i] * b[i];
			aNorm += a[i] * a[i];
			bNorm += b[i] * b[i];
		}
		if (aNorm == 0 || bNorm == 0) {
			return 0;
		}
		return dot / (Math.sqrt(aNorm) * Math.sqrt(bNorm));
	}

	private Integer maxZoneDistance(String time) {
		return switch (time) {
			case "짧게" -> SHORT_ZONE_RANGE;
			case "중간" -> MEDIUM_ZONE_RANGE;
			case "길게" -> null;
			default -> null;
		};
	}

	private boolean isInZoneRange(Store anchorStore, Store candidateStore, Integer maxZoneDistance) {
		return maxZoneDistance == null || zoneDistance(anchorStore, candidateStore) <= maxZoneDistance;
	}

	private int zoneDistance(Store anchorStore, Store candidateStore) {
		if (anchorStore.getZoneNumber() == null || candidateStore.getZoneNumber() == null) {
			return 0;
		}
		return Math.abs(anchorStore.getZoneNumber() - candidateStore.getZoneNumber());
	}

	private record ScoredStore(Store store, double score) {
	}

	private record CachedEmbedding(String sourceText, double[] embedding) {
	}
}