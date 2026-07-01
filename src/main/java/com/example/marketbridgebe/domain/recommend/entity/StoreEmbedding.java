package com.example.marketbridgebe.domain.recommend.entity;

import com.example.marketbridgebe.domain.store.entity.Store;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreEmbedding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id", nullable = false, unique = true)
	private Store store;

	@Lob
	@Column(nullable = false, columnDefinition = "TEXT")
	private String sourceText;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String embeddingJson;

	private LocalDateTime updatedAt;

	@Builder
	private StoreEmbedding(Store store, String sourceText, String embeddingJson) {
		this.store = store;
		this.sourceText = sourceText;
		this.embeddingJson = embeddingJson;
		this.updatedAt = LocalDateTime.now();
	}

	public boolean matchesSourceText(String sourceText) {
		return StringUtils.hasText(this.sourceText) && this.sourceText.equals(sourceText);
	}

	public void updateEmbedding(String sourceText, String embeddingJson) {
		this.sourceText = sourceText;
		this.embeddingJson = embeddingJson;
		this.updatedAt = LocalDateTime.now();
	}
}