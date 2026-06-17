package com.example.marketbridgebe.domain.store.controller;

import com.example.marketbridgebe.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TO-BE: GET /api/stores (목록 + 키워드 필터), GET /api/stores/{id} 구현
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
}
