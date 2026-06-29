package com.example.marketbridgebe.domain.market.service;

import com.example.marketbridgebe.domain.market.dto.KeywordResponseDto;
import com.example.marketbridgebe.domain.market.dto.MarketResponseDto;
import com.example.marketbridgebe.domain.market.repository.MarketRepository;
import com.example.marketbridgebe.domain.recommend.RecommendKeyword;
import com.example.marketbridgebe.global.exception.CustomException;
import com.example.marketbridgebe.global.exception.ErrorCode;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final MarketRepository marketRepository;

    @Transactional(readOnly = true)
    public MarketResponseDto getMarket(Long marketId) {
        return marketRepository.findById(marketId)
                .map(MarketResponseDto::from)
                .orElseThrow(() -> new CustomException(ErrorCode.MARKET_NOT_FOUND));
    }

    public Map<String, List<KeywordResponseDto>> getKeywords() {
        return Arrays.stream(RecommendKeyword.values())
                .collect(Collectors.groupingBy(
                        RecommendKeyword::getGroup,
                        LinkedHashMap::new,
                        Collectors.mapping(KeywordResponseDto::from, Collectors.toList())
                ));
    }
}
