package com.dopkit.business;

import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiResponse;
import com.dopkit.example.ApiRuntime;

/**
 * 业务层默认适配器
 * 提供通用的转换逻辑
 */
public class ApiDefaultAdapter {

    /**
     * 默认输出转换：直接将结果包装为成功响应
     */
    public static <TInnerOutput> ApiResponse stdMakeOuterOutput(
            ApiRuntime outerRuntime,
            ApiRequest outerInput,
            Object outerConfig,
            Object outerDerived,
            TInnerOutput innerOutput) {
        return ApiResponse.success(innerOutput);
    }
}
