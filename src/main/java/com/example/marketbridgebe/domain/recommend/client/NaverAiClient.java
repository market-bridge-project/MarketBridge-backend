package com.example.marketbridgebe.domain.recommend.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class NaverAiClient {

	private final RestClient restClient;
	private final String apiKey;
	private final String embeddingUrl;
	private final String embeddingModel;

	public NaverAiClient(
		@Value("${naver.ai.api-key:}") String apiKey,
		@Value("${naver.ai.base-url:https://clovastudio.stream.ntruss.com}") String baseUrl,
		@Value("${naver.ai.embedding-path:/testapp/v1/api-tools/embedding/v2}") String embeddingPath,
		@Value("${naver.ai.embedding-model:clir-emb-dolphin}") String embeddingModel) {
		this.restClient = RestClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
		this.apiKey = apiKey;
		this.embeddingUrl = embeddingPath;
		this.embeddingModel = embeddingModel;
	}

	public Optional<double[]> embed(String text) {
		if (!StringUtils.hasText(apiKey)) {
			return Optional.empty();
		}

		try {
			Map<?, ?> response = restClient.post()
				.uri(embeddingUrl)
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
				.body(Map.of("model", embeddingModel, "text", text))
				.retrieve()
				.body(Map.class);

			Object embedding = response == null ? null : findFirst(response, "embedding", "embeddings");
			if (embedding instanceof List<?> values) {
				return Optional.of(values.stream()
					.mapToDouble(value -> ((Number) value).doubleValue())
					.toArray());
			}
		} catch (HttpClientErrorException.TooManyRequests e) {
			log.warn("Naver embedding API rate limit exceeded. Fallback scoring will be used. response={}",
				e.getResponseBodyAsString());
		} catch (HttpClientErrorException e) {
			log.warn("Naver embedding API call failed with HTTP status {}. Fallback scoring will be used. response={}",
				e.getStatusCode(), e.getResponseBodyAsString());
		} catch (Exception e) {
			log.warn("Naver embedding API call failed. Fallback scoring will be used.", e);
		}
		return Optional.empty();
	}

	private Object findFirst(Object value, String... keys) {
		if (value instanceof Map<?, ?> map) {
			for (String key : keys) {
				if (map.containsKey(key)) {
					return map.get(key);
				}
			}
			for (Object nested : map.values()) {
				Object found = findFirst(nested, keys);
				if (found != null) {
					return found;
				}
			}
		}
		if (value instanceof List<?> list) {
			for (Object nested : list) {
				Object found = findFirst(nested, keys);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}
}