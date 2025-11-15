package com.dopkit.router;

/**
 * 通用的组件处理器接口
 * @param <TRuntime> 运行时上下文类型
 * @param <TRequest> 请求类型
 * @param <TMatchResult> 匹配结果类型
 * @param <TResponse> 响应类型
 */
@FunctionalInterface
public interface ComponentHandler<TRuntime, TRequest, TMatchResult, TResponse> {
    /**
     * 处理请求
     * @param runtime 运行时上下文
     * @param request 请求对象
     * @param matchResult 路由匹配结果
     * @return 响应结果
     */
    TResponse handle(TRuntime runtime, TRequest request, TMatchResult matchResult);
}
