package com.example.marketbridgebe.domain.store.repository;

import com.example.marketbridgebe.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // TO-BE: marketId 기준 조회, 키워드 기반 필터링 메서드 추가
}
