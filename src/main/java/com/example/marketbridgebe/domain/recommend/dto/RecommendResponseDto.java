package com.example.marketbridgebe.domain.recommend.dto;

import com.example.marketbridgebe.domain.store.entity.Store;
import java.util.List;

public record RecommendResponseDto(
	String courseTitle,
	List<StoreDto> stores
) {

	public record StoreDto(
		Long storeId,
		String name,
		String category,
		String thumbnailUrl
	) {

		public static StoreDto from(Store store) {
			return new StoreDto(store.getId(), store.getName(), store.getCategory(), store.getImageUrl());
		}
	}
}
