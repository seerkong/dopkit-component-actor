package com.dopkit.business;

import com.dopkit.example.ApiRequest;
import com.dopkit.example.ApiResponse;
import com.dopkit.example.ApiRuntime;
import com.dopkit.component.StdRunComponentLogic;

import java.util.Map;

/**
 * OOP风格的API适配器接口
 * 业务层只需实现 makeInnerInput 和 runCoreLogic 两个方法
 * 其他适配逻辑通过默认方法复用
 *
 * 使用示例：
 * public class UserGetByNameAdapter implements OOPStyleApiAdapter<GetUserRequest, User> {
 *     @Override
 *     public String getRoutePattern() {
 *         return "/user/{username}";
 *     }
 *
 *     @Override
 *     public GetUserRequest makeInnerInput(ApiRuntime runtime, ApiRequest request, Map<String, String> pathVars) {
 *         return new GetUserRequest(pathVars.get("username"));
 *     }
 *
 *     @Override
 *     public User runCoreLogic(ApiRuntime runtime, GetUserRequest input) {
 *         return runtime.getUserService().getUserByUsername(input.getUsername());
 *     }
 * }
 */
public interface OOPStyleApiAdapter<TInnerInput, TInnerOutput> extends ApiAdapter {

    /**
     * 将外部请求转换为内部输入
     * @param runtime 运行时上下文
     * @param request 请求对象
     * @param pathVariables 路径变量
     * @return 内部输入
     */
    TInnerInput makeInnerInput(ApiRuntime runtime, ApiRequest request, Map<String, String> pathVariables);

    /**
     * 执行核心业务逻辑
     * @param runtime 运行时上下文
     * @param input 内部输入
     * @return 内部输出
     */
    TInnerOutput runCoreLogic(ApiRuntime runtime, TInnerInput input);

    @Override
    default ApiResponse dispatch(ApiRuntime runtime, ApiRequest request, Map<String, String> pathVariables) {
        // 使用标准组件封装逻辑执行
        return StdRunComponentLogic.runByFuncStyleAdapter(
                runtime, request, null,
                // outerDerived: 将 pathVariables 作为计算值传递
                (ApiRuntime rt, ApiRequest req, Object config) -> pathVariables,
                // innerRuntime: 直接透传
                StdRunComponentLogic::stdMakeIdentityInnerRuntime,
                // innerInput: 调用 makeInnerInput，从 outerDerived 获取 pathVariables
                (ApiRuntime rt, ApiRequest req, Object config, Object computed) ->
                    makeInnerInput(rt, req, (Map<String, String>) computed),
                // innerConfig: 直接透传
                StdRunComponentLogic::stdMakeIdentityInnerConfig,
                // coreLogic: 调用 runCoreLogic
                (ApiRuntime rt, TInnerInput input, Object config) -> runCoreLogic(rt, input),
                // outerOutput: 使用默认转换逻辑
                ApiDefaultAdapter::stdMakeOuterOutput
        );
    }
}
