package com.dopkit.router;

/**
 * 通用的路由匹配器接口
 * @param <TRequest> 请求类型
 * @param <TMatchResult> 匹配结果类型
 */
@FunctionalInterface
public interface RouteMatcher<TRequest, TMatchResult> {
    /**
     * 检查请求是否匹配路由规则
     * @param request 请求对象
     * @return 匹配结果，如果不匹配则返回null
     */
    TMatchResult match(TRequest request);
}
