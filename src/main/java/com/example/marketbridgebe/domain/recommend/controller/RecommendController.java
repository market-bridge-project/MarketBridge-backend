package com.example.marketbridgebe.domain.recommend.controller;

import com.example.marketbridgebe.domain.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TO-BE: POST /api/recommend 구현
@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
}
