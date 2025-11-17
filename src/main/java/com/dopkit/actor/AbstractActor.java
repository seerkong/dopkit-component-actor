package com.dopkit.actor;

import com.dopkit.dispatch.ClassDispatchRequest;
import com.dopkit.dispatch.CommandDispatchRequest;
import com.dopkit.dispatch.DispatchEngine;
import com.dopkit.dispatch.DispatchRequest;
import com.dopkit.dispatch.DispatchResult;
import com.dopkit.dispatch.EnumDispatchRequest;
import com.dopkit.dispatch.RouteKeyDispatchRequest;
import com.dopkit.dispatch.RouteKeyToEnumDispatchRequest;

/**
 * Dop Actor 抽象基类
 * 提供4种内置分发机制的实现
 *
 * @param <TResult> 结果类型
 * @author kongweixian
 */
public abstract class AbstractActor<TResult> implements IActor<TResult> {

    private ActorRoute<TResult> route = null;

    /**
     * 子类实现此方法来配置路由
     */
    protected abstract ActorRoute<TResult> createActorRoute();

    /**
     * 创建错误结果（子类可覆盖）
     */
    protected abstract TResult createErrorResult(String message);

    /**
     * 延迟初始化路由表
     */
    private synchronized void initRoute() {
        if (route == null) {
            route = createActorRoute();
        }
    }

    /**
     * 分发机制1: By Class类型分发
     */
    @Override
    public <TOutput> TResult call(Object input) {
        if (route == null) {
            initRoute();
        }
        DispatchResult<TResult> result = dispatch(new ClassDispatchRequest<>(input));
        if (result.isHandled()) {
            return result.getResult();
        }
        Class<?> inputClass = input == null ? Void.class : input.getClass();
        return createErrorResult("No handler registered for input type: " + inputClass.getName());
    }

    /**
     * 分发机制2: By RouteKey字符串分发
     * 分发机制4: By RouteKey with Enum fallback
     */
    @Override
    public <TOutput> TResult callByRouteKey(String routeKey, Object input) {
        if (route == null) {
            initRoute();
        }

        DispatchResult<TResult> result =
                dispatch(RouteKeyDispatchRequest.direct(routeKey, input));
        if (result.isHandled()) {
            return result.getResult();
        }

        result = dispatch(new RouteKeyToEnumDispatchRequest<>(routeKey, input));
        if (result.isHandled()) {
            return result.getResult();
        }

        result = dispatch(RouteKeyDispatchRequest.withDefaults(routeKey, input));
        if (result.isHandled()) {
            return result.getResult();
        }

        return createErrorResult("No handler registered for routeKey: " + routeKey);
    }

    /**
     * 分发机制3: By 枚举类型分发
     */
    @Override
    public <TOutput, E extends Enum<E>> TResult callByEnum(E routeEnum, Object input) {
        if (route == null) {
            initRoute();
        }
        DispatchResult<TResult> result = dispatch(new EnumDispatchRequest<>(routeEnum, input));
        if (result.isHandled()) {
            return result.getResult();
        }
        return createErrorResult("No handler registered for enum: " + routeEnum);
    }

    /**
     * 分发机制5: By Command字符串分发（CommandTable模式）
     * 介于程序入口层和class级别分发之间的机制
     *
     * 工作流程：
     * 1. 使用commandConverter将字符串转换为枚举
     * 2. 使用commandHandlerExtractor从枚举中提取handler
     * 3. 执行handler处理input
     * 4. 如果转换失败或未找到handler，执行兜底handler
     */
    @Override
    public <TOutput> TResult callByCommand(String command, Object input) {
        if (route == null) {
            initRoute();
        }

        // 检查是否配置了CommandTable
        if (route.getCommandConverter() == null) {
            return createErrorResult("CommandTable not configured. " +
                    "Please call registerCommandTable() in createActorRoute()");
        }
        DispatchResult<TResult> result = dispatch(new CommandDispatchRequest<>(command, input));
        if (result.isHandled()) {
            return result.getResult();
        }
        return createErrorResult("No handler found for command: " + command);
    }

    private DispatchResult<TResult> dispatch(DispatchRequest<TResult> request) {
        DispatchEngine<TResult> engine = route.getDispatchEngine();
        if (engine == null) {
            return DispatchResult.notHandled();
        }
        return engine.dispatch(request);
    }
}
