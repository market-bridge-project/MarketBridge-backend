package com.example.marketbridgebe.global.response;

public record ApiResponse<T>(boolean isSuccess, String code, String message, T result) {

    private static final String SUCCESS_CODE = "COMMON200";
    private static final String SUCCESS_MESSAGE = "성공입니다.";

    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, SUCCESS_CODE, SUCCESS_MESSAGE, result);
    }

    public static <T> ApiResponse<T> of(String code, String message, T result) {
        return new ApiResponse<>(true, code, message, result);
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
