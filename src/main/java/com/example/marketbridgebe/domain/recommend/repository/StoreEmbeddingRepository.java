package com.example.marketbridgebe.domain.recommend.repository;

import com.example.marketbridgebe.domain.recommend.entity.StoreEmbedding;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreEmbeddingRepository extends JpaRepository<StoreEmbedding, Long> {

	Optional<StoreEmbedding> findByStoreId(Long storeId);
}