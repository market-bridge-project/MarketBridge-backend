package com.example.marketbridgebe.domain.market.service;

import com.example.marketbridgebe.domain.market.repository.MarketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TO-BE: 시장 목록/상세 조회 메서드 구현
@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;
}
