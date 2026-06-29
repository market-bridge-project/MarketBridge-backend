package com.example.marketbridgebe.domain.store.dto;

import com.example.marketbridgebe.domain.store.entity.Store;

public record StoreListResponseDto(
        Long id,
        String name,
        String category,
        Double mapX,
        Double mapY
) {

    public static StoreListResponseDto from(Store store) {
        return new StoreListResponseDto(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getMapX(),
                store.getMapY()
        );
    }
}
