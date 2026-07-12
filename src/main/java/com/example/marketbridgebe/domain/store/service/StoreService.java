package com.example.marketbridgebe.domain.store.service;

import com.example.marketbridgebe.domain.store.dto.StoreDetailResponseDto;
import com.example.marketbridgebe.domain.store.dto.StoreListResponseDto;
import com.example.marketbridgebe.domain.store.repository.StoreRepository;
import com.example.marketbridgebe.global.exception.CustomException;
import com.example.marketbridgebe.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<StoreListResponseDto> getStores(String category, String keyword) {
        // 빈 문자열/공백("")도 필터 없음(null)으로 취급 (Swagger가 ?category=&keyword= 로 빈 값을 보내는 경우 대응)
        String normalizedCategory = StringUtils.hasText(category) ? category : null;
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword : null;
        return storeRepository.findByFilters(normalizedCategory, normalizedKeyword).stream()
                .map(StoreListResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreDetailResponseDto getStoreDetail(Long storeId) {
        return storeRepository.findById(storeId)
                .map(StoreDetailResponseDto::from)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));
    }
}
