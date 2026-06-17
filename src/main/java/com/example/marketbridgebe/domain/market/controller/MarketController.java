package com.example.marketbridgebe.domain.market.controller;

import com.example.marketbridgebe.domain.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TO-BE: GET /api/markets, GET /api/markets/{id} 구현
@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;
}
