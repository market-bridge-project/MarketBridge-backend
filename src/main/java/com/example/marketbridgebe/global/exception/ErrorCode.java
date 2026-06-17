package com.example.marketbridgebe.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 점포입니다."),
    MARKET_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 시장입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    AI_RECOMMEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "추천 결과를 생성하지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
