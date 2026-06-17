package com.example.marketbridgebe.domain.store.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TO-BE: market(FK, @ManyToOne), name, category, latitude, longitude, intro, imageUrl 필드 추가
    // TO-BE: 필드 추가 후 @Builder 생성자 작성
}
