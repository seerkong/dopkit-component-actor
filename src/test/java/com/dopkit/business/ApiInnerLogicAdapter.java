package com.dopkit.business;

import com.dopkit.example.ApiRuntime;
import com.dopkit.component.StdInnerLogic;

/**
 * 业务层核心逻辑适配器
 * 简化为只需要关注 Runtime 和 Input 的处理
 */
@FunctionalInterface
public interface ApiInnerLogicAdapter<TInnerInput, TInnerOutput>
        extends StdInnerLogic<ApiRuntime, TInnerInput, Object, TInnerOutput> {

    /**
     * 业务层简化接口：执行核心逻辑
     */
    TInnerOutput runCoreLogic(ApiRuntime runtime, TInnerInput input);

    @Override
    default TInnerOutput stdInnerLogic(
            ApiRuntime runtime,
            TInnerInput input,
            Object config) {
        return runCoreLogic(runtime, input);
    }
}
