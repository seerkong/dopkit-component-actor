package com.dopkit.actor.example;

import com.dopkit.actor.AbstractActor;

/**
 * API端点基类
 * 业务封装层：实现AbstractActor，固定结果类型为Result<?>
 *
 * 业务代码继承此类：
 * public class UserApi extends ApiEndpointBase {
 *     protected ActorRoute<Result<?>> createActorRoute() {
 *         return ActorRouteBuilder.<Result<?>>create()
 *             .match(...)
 *             .build();
 *     }
 * }
 *
 * @author kongweixian
 */
public abstract class ApiEndpointBase extends AbstractActor<Result<?>> {

    @Override
    protected Result<?> createErrorResult(String message) {
        return Result.fail(message);
    }

    /**
     * 类型安全的call方法
     * 返回具体类型的Result
     */
    public <T> Result<T> callTyped(Object input) {
        @SuppressWarnings("unchecked")
        Result<T> result = (Result<T>) call(input);
        return result;
    }

    /**
     * 类型安全的callByRouteKey方法
     */
    public <T> Result<T> callByRouteKeyTyped(String routeKey, Object input) {
        @SuppressWarnings("unchecked")
        Result<T> result = (Result<T>) callByRouteKey(routeKey, input);
        return result;
    }

    /**
     * 类型安全的callByEnum方法
     */
    public <T, E extends Enum<E>> Result<T> callByEnumTyped(E routeEnum, Object input) {
        @SuppressWarnings("unchecked")
        Result<T> result = (Result<T>) callByEnum(routeEnum, input);
        return result;
    }

    /**
     * 类型安全的callByCommand方法
     */
    public <T> Result<T> callByCommandTyped(String command, Object input) {
        @SuppressWarnings("unchecked")
        Result<T> result = (Result<T>) callByCommand(command, input);
        return result;
    }
}
