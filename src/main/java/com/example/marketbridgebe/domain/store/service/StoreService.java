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

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<StoreListResponseDto> getStores(String category, String keyword) {
        return storeRepository.findByFilters(category, keyword).stream()
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
