package com.example.marketbridgebe.domain.market.repository;

import com.example.marketbridgebe.domain.market.entity.Market;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepository extends JpaRepository<Market, Long> {

    // TO-BE: 필요한 조회 메서드 추가 (지금은 기본 CRUD만 사용)
}
