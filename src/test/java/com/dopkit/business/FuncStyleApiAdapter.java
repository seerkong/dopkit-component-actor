package com.dopkit.business;

import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiResponse;
import com.dopkit.example.ApiRuntime;
import com.dopkit.component.StdRunComponentLogic;

import java.util.Map;

/**
 * 函数式风格的API适配器
 * 通过构造函数传入路由模式、输入适配器和核心逻辑适配器
 *
 * 使用示例：
 * new FuncStyleApiAdapter<>(
 *     "/user/search",
 *     UserSearchAdapter.InnerInputAdapter,
 *     UserSearchAdapter.CoreLogicAdapter
 * )
 */
public class FuncStyleApiAdapter<TInnerInput, TInnerOutput> implements ApiAdapter {

    private final String routePattern;
    private final ApiInputAdapter<TInnerInput> inputAdapter;
    private final ApiInnerLogicAdapter<TInnerInput, TInnerOutput> coreLogicAdapter;

    public FuncStyleApiAdapter(
            String routePattern,
            ApiInputAdapter<TInnerInput> inputAdapter,
            ApiInnerLogicAdapter<TInnerInput, TInnerOutput> coreLogicAdapter) {
        this.routePattern = routePattern;
        this.inputAdapter = inputAdapter;
        this.coreLogicAdapter = coreLogicAdapter;
    }

    @Override
    public String getRoutePattern() {
        return routePattern;
    }

    @Override
    public ApiResponse dispatch(ApiRuntime runtime, ApiRequest request, Map<String, String> pathVariables) {
        // 使用标准组件封装逻辑执行
        return StdRunComponentLogic.runByFuncStyleAdapter(
                runtime, request, null,
                StdRunComponentLogic::stdMakeNullOuterComputed,
                StdRunComponentLogic::stdMakeIdentityInnerRuntime,
                inputAdapter,
                StdRunComponentLogic::stdMakeIdentityInnerConfig,
                coreLogicAdapter,
                ApiDefaultAdapter::stdMakeOuterOutput
        );
    }
}
