package com.dopkit.business;

import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiResponse;
import com.dopkit.example.ApiRuntime;

import java.util.Map;

/**
 * 业务层API适配器基础接口
 * 固定了外层类型为 ApiRuntime, ApiRequest, ApiResponse
 * 简化业务代码的泛型参数
 */
public interface ApiAdapter {
    /**
     * 获取路由模式
     */
    String getRoutePattern();

    /**
     * 分发请求
     */
    ApiResponse dispatch(ApiRuntime runtime, ApiRequest request, Map<String, String> pathVariables);
}
