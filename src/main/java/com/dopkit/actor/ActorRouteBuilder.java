package com.dopkit.actor;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Actor路由构建器
 * 提供流式API来配置路由
 *
 * @param <TResult> 结果类型
 * @author kongweixian
 */
public class ActorRouteBuilder<TResult> {

    private final ActorRoute<TResult> route;

    private ActorRouteBuilder(ActorRoute<TResult> route) {
        this.route = route;
    }

    /**
     * 创建新的路由构建器
     */
    public static <TResult> ActorRouteBuilder<TResult> create() {
        return new ActorRouteBuilder<>(new ActorRoute<>());
    }

    /**
     * 注册handler - 支持Class类型、RouteKey、Enum三种分发方式
     *
     * @param inputClass 输入参数的Class类型
     * @param routeKeys 可选的RouteKey集合
     * @param routeEnums 可选的Enum集合
     * @param handler 处理函数
     * @param <TInput> 输入参数类型
     * @param <TOutput> 输出结果类型
     * @return 构建器自身，支持链式调用
     */
    public <TInput, TOutput, E extends Enum<E>> ActorRouteBuilder<TResult> match(
            final Class<TInput> inputClass,
            final Set<String> routeKeys,
            final Set<E> routeEnums,
            final Function<TInput, TResult> handler) {

        Objects.requireNonNull(inputClass, "inputClass");
        Objects.requireNonNull(handler, "handler");

        // 包装handler，添加类型检查
        Function<Object, TResult> wrappedHandler = input -> {
            if (input != null && !inputClass.isInstance(input)) {
                throw new IllegalArgumentException(
                        String.format("Handler for %s cannot process %s",
                                inputClass.getName(),
                                input.getClass().getName()));
            }
            return handler.apply(inputClass.cast(input));
        };

        // 注册到Class映射
        route.getClassToHandlerMap().put(inputClass, wrappedHandler);

        // 注册到RouteKey映射
        if (routeKeys != null) {
            for (String key : routeKeys) {
                if (key != null && !key.isEmpty()) {
                    route.getKeyToHandlerMap().put(key, wrappedHandler);
                }
            }
        }

        // 注册到Enum映射
        if (routeEnums != null) {
            for (E enumValue : routeEnums) {
                if (enumValue != null) {
                    route.getEnumToHandlerMap().put(enumValue, wrappedHandler);
                }
            }
        }

        return this;
    }

    /**
     * 简化版：只注册Class类型分发
     */
    public <TInput> ActorRouteBuilder<TResult> matchByClass(
            Class<TInput> inputClass,
            Function<TInput, TResult> handler) {
        return match(inputClass, null, null, handler);
    }

    /**
     * 简化版：注册Class类型 + RouteKey分发
     */
    public <TInput> ActorRouteBuilder<TResult> matchByClassAndKey(
            Class<TInput> inputClass,
            Set<String> routeKeys,
            Function<TInput, TResult> handler) {
        return match(inputClass, routeKeys, null, handler);
    }

    /**
     * 简化版：注册Class类型 + Enum分发
     */
    public <TInput, E extends Enum<E>> ActorRouteBuilder<TResult> matchByClassAndEnum(
            Class<TInput> inputClass,
            Set<E> routeEnums,
            Function<TInput, TResult> handler) {
        return match(inputClass, null, routeEnums, handler);
    }

    /**
     * 注册枚举转换器
     * 用于支持 callByRouteKey 时自动尝试将字符串转换为枚举
     *
     * @param enumClass 枚举类型
     * @param converter 转换函数：String -> Enum，转换失败返回null
     */
    public <E extends Enum<E>> ActorRouteBuilder<TResult> registerEnumConverter(
            Class<E> enumClass,
            Function<String, E> converter) {
        Objects.requireNonNull(enumClass, "enumClass");
        Objects.requireNonNull(converter, "converter");
        route.getEnumConverters().put(enumClass, converter);
        return this;
    }

    /**
     * 注册默认输入处理器
     */
    public ActorRouteBuilder<TResult> matchAny(Function<Object, TResult> handler) {
        Objects.requireNonNull(handler, "handler");
        route.setDefaultInputHandler(handler);
        return this;
    }

    /**
     * 注册默认RouteKey处理器
     */
    public ActorRouteBuilder<TResult> matchAnyKey(BiFunction<String, Object, TResult> handler) {
        Objects.requireNonNull(handler, "handler");
        route.setDefaultKeyHandler(handler);
        return this;
    }

    /**
     * 注册默认Enum处理器
     */
    public ActorRouteBuilder<TResult> matchAnyEnum(BiFunction<Enum<?>, Object, TResult> handler) {
        Objects.requireNonNull(handler, "handler");
        route.setDefaultEnumHandler(handler);
        return this;
    }

    /**
     * 机制5: 注册CommandTable模式配置
     * 介于程序入口层和class级别分发之间的机制
     *
     * 工作流程：
     * 1. commandConverter: 将字符串转换为枚举
     * 2. handlerExtractor: 从枚举中提取对应的handler
     * 3. defaultHandler: 当转换失败或未找到handler时的兜底处理
     *
     * 使用示例：
     * <pre>{@code
     * .registerCommandTable(
     *     cmd -> UserCommandType.valueOf(cmd.toUpperCase()),  // 字符串转枚举
     *     cmdEnum -> UserCommandHandlerTable.getHandler(cmdEnum),  // 从枚举提取handler
     *     (cmd, input) -> Result.fail("Unknown command: " + cmd)  // 兜底处理
     * )
     * }</pre>
     *
     * @param commandConverter 字符串到枚举的转换器，转换失败返回null
     * @param handlerExtractor 从枚举中提取handler的函数，未找到返回null
     * @param defaultHandler 兜底处理器
     * @param <E> 枚举类型
     * @return 构建器自身，支持链式调用
     */
    public <E extends Enum<E>> ActorRouteBuilder<TResult> registerCommandTable(
            Function<String, E> commandConverter,
            Function<E, Function<Object, TResult>> handlerExtractor,
            BiFunction<String, Object, TResult> defaultHandler) {

        Objects.requireNonNull(commandConverter, "commandConverter");
        Objects.requireNonNull(handlerExtractor, "handlerExtractor");
        Objects.requireNonNull(defaultHandler, "defaultHandler");

        // 将有类型的converter转换为无类型的存储
        @SuppressWarnings("unchecked")
        Function<String, ? extends Enum<?>> converter = (Function<String, ? extends Enum<?>>) commandConverter;
        route.setCommandConverter(converter);

        // 将有类型的extractor转换为无类型的存储
        @SuppressWarnings("unchecked")
        Function<Enum<?>, Function<Object, TResult>> extractor =
                (Function<Enum<?>, Function<Object, TResult>>) (Function<?, ?>) handlerExtractor;
        route.setCommandHandlerExtractor(extractor);

        route.setCommandDefaultHandler(defaultHandler);
        return this;
    }

    /**
     * 构建并返回ActorRoute实例
     */
    public ActorRoute<TResult> build() {
        return route;
    }
}
