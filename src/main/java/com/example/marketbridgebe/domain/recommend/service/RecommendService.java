package com.example.marketbridgebe.domain.recommend.service;

import com.example.marketbridgebe.domain.recommend.client.UpstageAiClient;
import com.example.marketbridgebe.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TO-BE: 키워드 받기 -> store 조회 -> 프롬프트 구성 -> AiClient 호출 -> 결과 매핑
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final StoreRepository storeRepository;
    private final UpstageAiClient upstageAiClient;
}
