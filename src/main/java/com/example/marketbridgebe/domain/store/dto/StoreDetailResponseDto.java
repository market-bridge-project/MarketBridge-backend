package com.example.marketbridgebe.domain.store.dto;

import com.example.marketbridgebe.domain.recommend.RecommendKeyword;
import com.example.marketbridgebe.domain.store.entity.Store;
import com.example.marketbridgebe.domain.store.entity.StoreMenu;
import java.util.List;

public record StoreDetailResponseDto(
        Long id,
        String name,
        String category,
        String intro,
        String imageUrl,
        Double mapX,
        Double mapY,
        String openTime,
        String phoneNumber,
        List<KeywordDto> keywords,
        List<MenuDto> menus
) {

    public static StoreDetailResponseDto from(Store store) {
        List<KeywordDto> keywords = store.getKeywords().stream()
                .map(KeywordDto::from)
                .toList();
        List<MenuDto> menus = store.getMenus().stream()
                .map(MenuDto::from)
                .toList();

        return new StoreDetailResponseDto(
                store.getId(),
                store.getName(),
                store.getCategory(),
                store.getIntro(),
                store.getImageUrl(),
                store.getMapX(),
                store.getMapY(),
                store.getOpenTime(),
                store.getPhoneNumber(),
                keywords,
                menus
        );
    }

    public record KeywordDto(String name, String label) {

        public static KeywordDto from(RecommendKeyword keyword) {
            return new KeywordDto(keyword.name(), keyword.getLabel());
        }
    }

    public record MenuDto(String name, Integer price) {

        public static MenuDto from(StoreMenu menu) {
            return new MenuDto(menu.getName(), menu.getPrice());
        }
    }
}
