package com.dopkit.example;

import lombok.Builder;
import lombok.Data;

/**
 * 示例API响应对象
 */
@Data
@Builder
public class ApiResponse {
    private int code;
    private String message;
    private Object data;

    public static ApiResponse success(Object data) {
        return ApiResponse.builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static ApiResponse error(int code, String message) {
        return ApiResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}
