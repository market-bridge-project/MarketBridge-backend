package com.example.marketbridgebe.domain.market.controller;

import com.example.marketbridgebe.domain.market.dto.KeywordResponseDto;
import com.example.marketbridgebe.domain.market.dto.MarketResponseDto;
import com.example.marketbridgebe.domain.market.service.MarketService;
import com.example.marketbridgebe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Market", description = "시장 및 키워드 조회 API")
@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @Operation(summary = "시장 정보 조회", description = "marketId로 시장의 기본 정보를 조회합니다.")
    @GetMapping("/{marketId}")
    public ApiResponse<MarketResponseDto> getMarket(@PathVariable Long marketId) {
        return ApiResponse.success(marketService.getMarket(marketId));
    }

    @Operation(summary = "키워드 목록 조회", description = "추천 키워드를 그룹(WHO/WHAT/DURATION)별로 조회합니다.")
    @GetMapping("/keyword")
    public ApiResponse<Map<String, List<KeywordResponseDto>>> getKeywords() {
        return ApiResponse.success(marketService.getKeywords());
    }
}
