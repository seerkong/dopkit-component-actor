package com.dopkit.example;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 示例API请求对象
 */
@Data
@Builder
public class ApiRequest {
    private String path;
    private String method;
    private Map<String, String> pathVariables;
    private Map<String, String> queryParams;
    private String body;
}
