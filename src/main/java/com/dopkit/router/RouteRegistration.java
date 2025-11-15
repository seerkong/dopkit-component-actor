package com.dopkit.router;

/**
 * 路由注册项，包含匹配器和处理器
 * @param <TRuntime> 运行时上下文类型
 * @param <TRequest> 请求类型
 * @param <TMatchResult> 匹配结果类型
 * @param <TResponse> 响应类型
 */
public class RouteRegistration<TRuntime, TRequest, TMatchResult, TResponse> {
    private final RouteMatcher<TRequest, TMatchResult> matcher;
    private final ComponentHandler<TRuntime, TRequest, TMatchResult, TResponse> handler;

    public RouteRegistration(
            RouteMatcher<TRequest, TMatchResult> matcher,
            ComponentHandler<TRuntime, TRequest, TMatchResult, TResponse> handler) {
        this.matcher = matcher;
        this.handler = handler;
    }

    public RouteMatcher<TRequest, TMatchResult> getMatcher() {
        return matcher;
    }

    public ComponentHandler<TRuntime, TRequest, TMatchResult, TResponse> getHandler() {
        return handler;
    }

    /**
     * 尝试匹配并处理请求
     * @param runtime 运行时上下文
     * @param request 请求对象
     * @return 如果匹配成功，返回处理结果；否则返回null
     */
    public TResponse tryHandle(TRuntime runtime, TRequest request) {
        TMatchResult matchResult = matcher.match(request);
        if (matchResult != null) {
            return handler.handle(runtime, request, matchResult);
        }
        return null;
    }
}
