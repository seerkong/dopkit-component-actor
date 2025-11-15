package com.dopkit.actor;

import java.util.Map;
import java.util.function.Function;

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

        Class<?> inputClass = input == null ? Void.class : input.getClass();
        Function<Object, TResult> handler = route.getClassToHandlerMap().get(inputClass);

        if (handler == null) {
            // 尝试默认处理器
            if (route.getDefaultInputHandler() != null) {
                return route.getDefaultInputHandler().apply(input);
            }
            return createErrorResult("No handler registered for input type: " + inputClass.getName());
        }

        return handler.apply(input);
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

        // 1. 先尝试直接匹配字符串key
        Function<Object, TResult> handler = route.getKeyToHandlerMap().get(routeKey);
        if (handler != null) {
            return handler.apply(input);
        }

        // 2. 尝试转换为枚举（机制4）
        TResult enumResult = tryConvertAndCallByEnum(routeKey, input);
        if (enumResult != null) {
            return enumResult;
        }

        // 3. 尝试默认处理器
        if (route.getDefaultKeyHandler() != null) {
            return route.getDefaultKeyHandler().apply(routeKey, input);
        }
        if (route.getDefaultInputHandler() != null) {
            return route.getDefaultInputHandler().apply(input);
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

        Function<Object, TResult> handler = route.getEnumToHandlerMap().get(routeEnum);

        if (handler == null) {
            // 尝试默认处理器
            if (route.getDefaultEnumHandler() != null) {
                return route.getDefaultEnumHandler().apply(routeEnum, input);
            }
            if (route.getDefaultInputHandler() != null) {
                return route.getDefaultInputHandler().apply(input);
            }
            return createErrorResult("No handler registered for enum: " + routeEnum);
        }

        return handler.apply(input);
    }

    /**
     * 尝试将字符串转换为枚举并分发（机制4的实现）
     */
    private TResult tryConvertAndCallByEnum(String routeKey, Object input) {
        Map<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> converters =
                route.getEnumConverters();

        // 遍历所有注册的枚举转换器
        for (Map.Entry<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> entry
                : converters.entrySet()) {

            Function<String, ? extends Enum<?>> converter = entry.getValue();
            Enum<?> enumValue = converter.apply(routeKey);

            if (enumValue != null) {
                // 转换成功，使用枚举分发
                Function<Object, TResult> handler = route.getEnumToHandlerMap().get(enumValue);
                if (handler != null) {
                    return handler.apply(input);
                }
            }
        }

        return null; // 无法转换为任何枚举
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

        // 1. 尝试将字符串转换为枚举
        Function<String, ? extends Enum<?>> converter = route.getCommandConverter();
        Enum<?> commandEnum = converter.apply(command);

        // 2. 如果转换成功，尝试从枚举中提取handler
        if (commandEnum != null) {
            Function<Enum<?>, Function<Object, TResult>> extractor = route.getCommandHandlerExtractor();
            if (extractor != null) {
                Function<Object, TResult> handler = extractor.apply(commandEnum);
                if (handler != null) {
                    // 找到handler，执行
                    return handler.apply(input);
                }
            }
        }

        // 3. 转换失败或未找到handler，使用兜底处理器
        if (route.getCommandDefaultHandler() != null) {
            return route.getCommandDefaultHandler().apply(command, input);
        }

        // 4. 如果连兜底处理器都没有，返回错误
        return createErrorResult("No handler found for command: " + command);
    }
}
