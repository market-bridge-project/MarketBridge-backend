package com.example.marketbridgebe.domain.store.controller;

import com.example.marketbridgebe.domain.store.dto.StoreDetailResponseDto;
import com.example.marketbridgebe.domain.store.dto.StoreListResponseDto;
import com.example.marketbridgebe.domain.store.service.StoreService;
import com.example.marketbridgebe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Store", description = "점포 조회 API")
@RestController
@RequestMapping("/api/v1/market/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "점포 목록 조회", description = "지도용 경량 점포 목록을 조회합니다. category, keyword로 필터링할 수 있습니다.")
    @GetMapping
    public ApiResponse<List<StoreListResponseDto>> getStores(
            @Parameter(description = "업종 필터 (예: 분식)") @RequestParam(required = false) String category,
            @Parameter(description = "가게명 부분검색") @RequestParam(required = false) String keyword) {
        return ApiResponse.success(storeService.getStores(category, keyword));
    }

    @Operation(summary = "점포 상세 조회", description = "storeId로 점포의 상세 정보(키워드, 메뉴 포함)를 조회합니다.")
    @GetMapping("/{storeId}")
    public ApiResponse<StoreDetailResponseDto> getStoreDetail(@PathVariable Long storeId) {
        return ApiResponse.success(storeService.getStoreDetail(storeId));
    }
}
