package com.dopkit.actor;

import com.dopkit.dispatch.DispatchEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Actor路由表
 * 存储输入类型、RouteKey、Enum到Handler的映射关系
 *
 * @param <TResult> 结果类型
 * @author kongweixian
 */
public class ActorRoute<TResult> {

    /**
     * 输入类型到Handler的映射
     */
    private final Map<Class<?>, Function<Object, TResult>> classToHandlerMap = new HashMap<>();

    /**
     * RouteKey到Handler的映射
     */
    private final Map<String, Function<Object, TResult>> keyToHandlerMap = new HashMap<>();

    /**
     * Enum到Handler的映射
     */
    private final Map<Enum<?>, Function<Object, TResult>> enumToHandlerMap = new HashMap<>();

    /**
     * Enum Class 到 字符串转换器的映射
     * 用于支持 callByRouteKey 时自动尝试转换为枚举
     */
    private final Map<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> enumConverters = new HashMap<>();

    /**
     * 默认输入处理器，用于兜底处理未匹配到的输入类型
     */
    private Function<Object, TResult> defaultInputHandler;

    /**
     * 默认RouteKey处理器，用于兜底处理未匹配到的RouteKey
     */
    private BiFunction<String, Object, TResult> defaultKeyHandler;

    /**
     * 默认Enum处理器，用于兜底处理未匹配到的Enum
     */
    private BiFunction<Enum<?>, Object, TResult> defaultEnumHandler;

    /**
     * 机制5: CommandTable模式配置
     * 字符串到枚举的转换器
     */
    private Function<String, ? extends Enum<?>> commandConverter;

    /**
     * 机制5: CommandTable模式配置
     * 从枚举中提取Handler的函数
     */
    private Function<Enum<?>, Function<Object, TResult>> commandHandlerExtractor;

    /**
     * 机制5: CommandTable模式配置
     * 兜底处理器（当转换失败或未找到handler时使用）
     */
    private BiFunction<String, Object, TResult> commandDefaultHandler;

    /**
     * Dispatch engine shared by actors.
     */
    private DispatchEngine<TResult> dispatchEngine;

    // Getters
    public Map<Class<?>, Function<Object, TResult>> getClassToHandlerMap() {
        return classToHandlerMap;
    }

    public Map<String, Function<Object, TResult>> getKeyToHandlerMap() {
        return keyToHandlerMap;
    }

    public Map<Enum<?>, Function<Object, TResult>> getEnumToHandlerMap() {
        return enumToHandlerMap;
    }

    public Map<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> getEnumConverters() {
        return enumConverters;
    }

    public Function<Object, TResult> getDefaultInputHandler() {
        return defaultInputHandler;
    }

    public void setDefaultInputHandler(Function<Object, TResult> defaultInputHandler) {
        this.defaultInputHandler = defaultInputHandler;
    }

    public BiFunction<String, Object, TResult> getDefaultKeyHandler() {
        return defaultKeyHandler;
    }

    public void setDefaultKeyHandler(BiFunction<String, Object, TResult> defaultKeyHandler) {
        this.defaultKeyHandler = defaultKeyHandler;
    }

    public BiFunction<Enum<?>, Object, TResult> getDefaultEnumHandler() {
        return defaultEnumHandler;
    }

    public void setDefaultEnumHandler(BiFunction<Enum<?>, Object, TResult> defaultEnumHandler) {
        this.defaultEnumHandler = defaultEnumHandler;
    }

    public Function<String, ? extends Enum<?>> getCommandConverter() {
        return commandConverter;
    }

    public void setCommandConverter(Function<String, ? extends Enum<?>> commandConverter) {
        this.commandConverter = commandConverter;
    }

    public Function<Enum<?>, Function<Object, TResult>> getCommandHandlerExtractor() {
        return commandHandlerExtractor;
    }

    public void setCommandHandlerExtractor(Function<Enum<?>, Function<Object, TResult>> commandHandlerExtractor) {
        this.commandHandlerExtractor = commandHandlerExtractor;
    }

    public BiFunction<String, Object, TResult> getCommandDefaultHandler() {
        return commandDefaultHandler;
    }

    public void setCommandDefaultHandler(BiFunction<String, Object, TResult> commandDefaultHandler) {
        this.commandDefaultHandler = commandDefaultHandler;
    }

    public DispatchEngine<TResult> getDispatchEngine() {
        return dispatchEngine;
    }

    public void setDispatchEngine(DispatchEngine<TResult> dispatchEngine) {
        this.dispatchEngine = dispatchEngine;
    }
}
