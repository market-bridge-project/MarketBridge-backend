package com.example.marketbridgebe.domain.market.dto;

import com.example.marketbridgebe.domain.market.entity.Market;

public record MarketResponseDto(
        Long id,
        String name,
        String address
) {

    public static MarketResponseDto from(Market market) {
        return new MarketResponseDto(
                market.getId(),
                market.getName(),
                market.getAddress()
        );
    }
}
