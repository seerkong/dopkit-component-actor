package com.dopkit.router;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用的路由分发器
 * 支持注册多个路由规则，并按顺序匹配分发
 *
 * @param <TRuntime> 运行时上下文类型
 * @param <TRequest> 请求类型
 * @param <TMatchResult> 匹配结果类型
 * @param <TResponse> 响应类型
 */
public class GenericRouter<TRuntime, TRequest, TMatchResult, TResponse> {
    private final List<RouteRegistration<TRuntime, TRequest, TMatchResult, TResponse>> registrations;

    public GenericRouter() {
        this.registrations = new ArrayList<>();
    }

    /**
     * 注册路由（函数式风格）
     * @param matcher 匹配器
     * @param handler 处理器
     * @return 当前路由器实例，支持链式调用
     */
    public GenericRouter<TRuntime, TRequest, TMatchResult, TResponse> register(
            RouteMatcher<TRequest, TMatchResult> matcher,
            ComponentHandler<TRuntime, TRequest, TMatchResult, TResponse> handler) {
        registrations.add(new RouteRegistration<>(matcher, handler));
        return this;
    }

    /**
     * 注册路由（OOP风格）
     * @param registration 路由注册项
     * @return 当前路由器实例，支持链式调用
     */
    public GenericRouter<TRuntime, TRequest, TMatchResult, TResponse> register(
            RouteRegistration<TRuntime, TRequest, TMatchResult, TResponse> registration) {
        registrations.add(registration);
        return this;
    }

    /**
     * 分发请求到匹配的处理器
     * @param runtime 运行时上下文
     * @param request 请求对象
     * @return 处理结果，如果没有匹配的路由则返回null
     */
    public TResponse dispatch(TRuntime runtime, TRequest request) {
        for (RouteRegistration<TRuntime, TRequest, TMatchResult, TResponse> registration : registrations) {
            TResponse response = registration.tryHandle(runtime, request);
            if (response != null) {
                return response;
            }
        }
        return null;
    }

    /**
     * 获取已注册的路由数量
     */
    public int getRegistrationCount() {
        return registrations.size();
    }
}
