package com.example.marketbridgebe.domain.store.repository;

import com.example.marketbridgebe.domain.store.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT DISTINCT s FROM Store s "
            + "LEFT JOIN FETCH s.menus "
            + "WHERE (:category IS NULL OR s.category = :category) "
            + "AND (:keyword IS NULL OR s.name LIKE CONCAT('%', :keyword, '%'))")
    List<Store> findByFilters(@Param("category") String category, @Param("keyword") String keyword);
}
