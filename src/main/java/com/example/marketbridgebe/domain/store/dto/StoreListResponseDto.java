package com.example.marketbridgebe.domain.store.dto;

import com.example.marketbridgebe.domain.store.entity.Store;
import com.example.marketbridgebe.domain.store.entity.StoreMenu;
import java.util.List;

public record StoreListResponseDto(
        Long id,
        String name,
        String category,
        String openTime,
        String imageUrl,
        Integer zoneNumber,
        List<String> menuNames
) {

    public static StoreListResponseDto from(Store store) {
        List<String> menuNames = store.getMenus().stream()
                .map(StoreMenu::getName)
                .toList();

        return new StoreListResponseDto(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getOpenTime(),
                store.getImageUrl(),
                store.getZoneNumber(),
                menuNames
        );
    }
}
