package com.example.marketbridgebe.domain.recommend;

import lombok.Getter;

@Getter
public enum RecommendKeyword {

    // 누구와 (WHO)
    ALONE("혼자", "WHO"),
    FRIEND("친구랑", "WHO"),
    FAMILY("가족과", "WHO"),
    COUPLE("연인과", "WHO"),

    // 무엇을 (WHAT)
    MEAL("식사", "WHAT"),
    SNACK("간식", "WHAT"),
    SHOPPING("쇼핑", "WHAT"),
    ETC("기타", "WHAT"),

    // 얼마나 (DURATION)
    SHORT("짧게", "DURATION"),
    MEDIUM("중간", "DURATION"),
    LONG("길게", "DURATION");

    private final String label;
    private final String group;

    RecommendKeyword(String label, String group) {
        this.label = label;
        this.group = group;
    }
}
