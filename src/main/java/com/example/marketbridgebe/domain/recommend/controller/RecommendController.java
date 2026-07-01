package com.example.marketbridgebe.domain.recommend.controller;

import com.example.marketbridgebe.domain.recommend.dto.RecommendRequestDto;
import com.example.marketbridgebe.domain.recommend.dto.RecommendResponseDto;
import com.example.marketbridgebe.domain.recommend.service.RecommendService;
import com.example.marketbridgebe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recommend", description = "방문 목적 기반 점포 코스 추천 API")
@RestController

@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {

	private final RecommendService recommendService;

	@Operation(summary = "점포 코스 추천", description = "who/what/time 방문 목적을 기반으로 점포 코스를 추천합니다.")
	@PostMapping
	public ApiResponse<RecommendResponseDto> recommendCourse(@RequestBody RecommendRequestDto request) {
		return ApiResponse.success(recommendService.recommendCourse(request));
	}
}
