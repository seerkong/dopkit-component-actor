package com.dopkit.business;

import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiRuntime;
import com.dopkit.component.StdInnerInputAdapter;

/**
 * 业务层输入适配器
 * 简化为只需要关注 Runtime 和 Request 的转换
 */
@FunctionalInterface
public interface ApiInputAdapter<TInnerInput>
        extends StdInnerInputAdapter<ApiRuntime, ApiRequest, Object, Object, TInnerInput> {

    /**
     * 业务层简化接口：将 ApiRuntime 和 ApiRequest 转换为内部输入
     */
    TInnerInput makeInnerInput(ApiRuntime runtime, ApiRequest request);

    @Override
    default TInnerInput stdMakeInnerInput(
            ApiRuntime outerRuntime,
            ApiRequest outerInput,
            Object outerConfig,
            Object outerDerived) {
        return makeInnerInput(outerRuntime, outerInput);
    }
}
