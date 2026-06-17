package com.example.marketbridgebe.domain.store.service;

import com.example.marketbridgebe.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TO-BE: 점포 목록/상세 조회, 키워드 필터링 메서드 구현
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
}
