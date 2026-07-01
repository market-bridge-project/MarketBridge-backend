package com.example.marketbridgebe.domain.store.entity;

import com.example.marketbridgebe.domain.market.entity.Market;
import com.example.marketbridgebe.domain.recommend.RecommendKeyword;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "market_id")
    private Market market;

    private String name;

    private String category;

    private String intro;

    private String imageUrl;

    private Double mapX;

    private Double mapY;

    private String openTime;

    private String phoneNumber;

    private Integer zoneNumber;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "store_keywords", joinColumns = @JoinColumn(name = "store_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "keyword")
    private Set<RecommendKeyword> keywords = new LinkedHashSet<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<StoreMenu> menus = new ArrayList<>();

    @Builder
    private Store(Market market, String name, String category, String intro, String imageUrl,
                  Double mapX, Double mapY, String openTime, String phoneNumber,
                  Integer zoneNumber, Set<RecommendKeyword> keywords) {
        this.market = market;
        this.name = name;
        this.category = category;
        this.intro = intro;
        this.imageUrl = imageUrl;
        this.mapX = mapX;
        this.mapY = mapY;
        this.openTime = openTime;
        this.phoneNumber = phoneNumber;
        this.zoneNumber = zoneNumber;
        if (keywords != null) {
            this.keywords = keywords;
        }
    }
}
