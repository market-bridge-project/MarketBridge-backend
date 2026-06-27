package com.example.marketbridgebe.domain.market.dto;

import com.example.marketbridgebe.domain.recommend.RecommendKeyword;

public record KeywordResponseDto(
        String name,
        String label
) {

    public static KeywordResponseDto from(RecommendKeyword keyword) {
        return new KeywordResponseDto(keyword.name(), keyword.getLabel());
    }
}
