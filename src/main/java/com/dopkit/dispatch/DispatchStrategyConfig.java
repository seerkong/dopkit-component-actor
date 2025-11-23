package com.dopkit.dispatch;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Strategy configuration tagged by {@link DispatchStrategyType}.
 * Only the configuration object matching the type is populated.
 */
public final class DispatchStrategyConfig<TResult> {
    private final DispatchStrategyType type;
    private final ClassDispatchConfig<TResult> classConfig;
    private final RouteKeyDispatchConfig<TResult> routeKeyConfig;
    private final EnumDispatchConfig<TResult> enumConfig;
    private final RouteKeyToEnumDispatchConfig<TResult> routeKeyToEnumConfig;
    private final CommandDispatchConfig<TResult> commandDispatchConfig;
    private final PathDispatchConfig<TResult> pathDispatchConfig;
    private final ActionPathDispatchConfig<TResult> actionPathDispatchConfig;

    private DispatchStrategyConfig(
            DispatchStrategyType type,
            ClassDispatchConfig<TResult> classConfig,
            RouteKeyDispatchConfig<TResult> routeKeyConfig,
            EnumDispatchConfig<TResult> enumConfig,
            RouteKeyToEnumDispatchConfig<TResult> routeKeyToEnumConfig,
            CommandDispatchConfig<TResult> commandDispatchConfig,
            PathDispatchConfig<TResult> pathDispatchConfig,
            ActionPathDispatchConfig<TResult> actionPathDispatchConfig) {
        this.type = type;
        this.classConfig = classConfig;
        this.routeKeyConfig = routeKeyConfig;
        this.enumConfig = enumConfig;
        this.routeKeyToEnumConfig = routeKeyToEnumConfig;
        this.commandDispatchConfig = commandDispatchConfig;
        this.pathDispatchConfig = pathDispatchConfig;
        this.actionPathDispatchConfig = actionPathDispatchConfig;
    }

    public static <TResult> DispatchStrategyConfig<TResult> forClassStrategy(
            ClassDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.CLASS,
                config,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    public static <TResult> DispatchStrategyConfig<TResult> forRouteKeyStrategy(
            RouteKeyDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.ROUTE_KEY,
                null,
                config,
                null,
                null,
                null,
                null,
                null);
    }

    public static <TResult> DispatchStrategyConfig<TResult> forEnumStrategy(
            EnumDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.ENUM,
                null,
                null,
                config,
                null,
                null,
                null,
                null);
    }

    public static <TResult> DispatchStrategyConfig<TResult> forRouteKeyToEnumStrategy(
            RouteKeyToEnumDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.ROUTE_KEY_TO_ENUM,
                null,
                null,
                null,
                config,
                null,
                null,
                null);
    }

    public static <TResult> DispatchStrategyConfig<TResult> forCommandStrategy(
            CommandDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.COMMAND_TABLE,
                null,
                null,
                null,
                null,
                config,
                null,
                null);
    }

    public static <TResult> DispatchStrategyConfig<TResult> forPathStrategy(
            PathDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.PATH,
                null,
                null,
                null,
                null,
                null,
                config,
                null);
    }

    public static <TResult> DispatchStrategyConfig<TResult> forActionPathStrategy(
            ActionPathDispatchConfig<TResult> config) {
        return new DispatchStrategyConfig<>(
                DispatchStrategyType.ACTION_PATH,
                null,
                null,
                null,
                null,
                null,
                null,
                config);
    }

    public DispatchStrategyType getType() {
        return type;
    }

    public ClassDispatchConfig<TResult> getClassConfig() {
        return classConfig;
    }

    public RouteKeyDispatchConfig<TResult> getRouteKeyConfig() {
        return routeKeyConfig;
    }

    public EnumDispatchConfig<TResult> getEnumConfig() {
        return enumConfig;
    }

    public RouteKeyToEnumDispatchConfig<TResult> getRouteKeyToEnumConfig() {
        return routeKeyToEnumConfig;
    }

    public CommandDispatchConfig<TResult> getCommandDispatchConfig() {
        return commandDispatchConfig;
    }

    public PathDispatchConfig<TResult> getPathDispatchConfig() {
        return pathDispatchConfig;
    }

    public ActionPathDispatchConfig<TResult> getActionPathDispatchConfig() {
        return actionPathDispatchConfig;
    }

    public static final class ClassDispatchConfig<TResult> {
        private final Map<Class<?>, Function<Object, TResult>> handlerMap;
        private final Function<Object, TResult> defaultHandler;

        public ClassDispatchConfig(
                Map<Class<?>, Function<Object, TResult>> handlerMap,
                Function<Object, TResult> defaultHandler) {
            this.handlerMap = handlerMap;
            this.defaultHandler = defaultHandler;
        }

        public Map<Class<?>, Function<Object, TResult>> getHandlerMap() {
            return handlerMap;
        }

        public Function<Object, TResult> getDefaultHandler() {
            return defaultHandler;
        }
    }

    public static final class RouteKeyDispatchConfig<TResult> {
        private final Map<String, Function<Object, TResult>> handlerMap;
        private final BiFunction<String, Object, TResult> defaultKeyHandler;
        private final Function<Object, TResult> defaultInputHandler;

        public RouteKeyDispatchConfig(
                Map<String, Function<Object, TResult>> handlerMap,
                BiFunction<String, Object, TResult> defaultKeyHandler,
                Function<Object, TResult> defaultInputHandler) {
            this.handlerMap = handlerMap;
            this.defaultKeyHandler = defaultKeyHandler;
            this.defaultInputHandler = defaultInputHandler;
        }

        public Map<String, Function<Object, TResult>> getHandlerMap() {
            return handlerMap;
        }

        public BiFunction<String, Object, TResult> getDefaultKeyHandler() {
            return defaultKeyHandler;
        }

        public Function<Object, TResult> getDefaultInputHandler() {
            return defaultInputHandler;
        }
    }

    public static final class EnumDispatchConfig<TResult> {
        private final Map<Enum<?>, Function<Object, TResult>> handlerMap;
        private final BiFunction<Enum<?>, Object, TResult> defaultEnumHandler;
        private final Function<Object, TResult> defaultInputHandler;

        public EnumDispatchConfig(
                Map<Enum<?>, Function<Object, TResult>> handlerMap,
                BiFunction<Enum<?>, Object, TResult> defaultEnumHandler,
                Function<Object, TResult> defaultInputHandler) {
            this.handlerMap = handlerMap;
            this.defaultEnumHandler = defaultEnumHandler;
            this.defaultInputHandler = defaultInputHandler;
        }

        public Map<Enum<?>, Function<Object, TResult>> getHandlerMap() {
            return handlerMap;
        }

        public BiFunction<Enum<?>, Object, TResult> getDefaultEnumHandler() {
            return defaultEnumHandler;
        }

        public Function<Object, TResult> getDefaultInputHandler() {
            return defaultInputHandler;
        }
    }

    public static final class RouteKeyToEnumDispatchConfig<TResult> {
        private final Map<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> converters;
        private final Map<Enum<?>, Function<Object, TResult>> enumHandlerMap;

        public RouteKeyToEnumDispatchConfig(
                Map<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> converters,
                Map<Enum<?>, Function<Object, TResult>> enumHandlerMap) {
            this.converters = converters;
            this.enumHandlerMap = enumHandlerMap;
        }

        public Map<Class<? extends Enum<?>>, Function<String, ? extends Enum<?>>> getConverters() {
            return converters;
        }

        public Map<Enum<?>, Function<Object, TResult>> getEnumHandlerMap() {
            return enumHandlerMap;
        }
    }

    public static final class CommandDispatchConfig<TResult> {
        private final Function<String, ? extends Enum<?>> commandConverter;
        private final Function<Enum<?>, Function<Object, TResult>> handlerExtractor;
        private final BiFunction<String, Object, TResult> defaultHandler;

        public CommandDispatchConfig(
                Function<String, ? extends Enum<?>> commandConverter,
                Function<Enum<?>, Function<Object, TResult>> handlerExtractor,
                BiFunction<String, Object, TResult> defaultHandler) {
            this.commandConverter = commandConverter;
            this.handlerExtractor = handlerExtractor;
            this.defaultHandler = defaultHandler;
        }

        public Function<String, ? extends Enum<?>> getCommandConverter() {
            return commandConverter;
        }

        public Function<Enum<?>, Function<Object, TResult>> getHandlerExtractor() {
            return handlerExtractor;
        }

        public BiFunction<String, Object, TResult> getDefaultHandler() {
            return defaultHandler;
        }
    }

    public static final class PathDispatchConfig<TResult> {
        private final List<PathDispatchHandler<TResult>> handlers;

        public PathDispatchConfig(List<PathDispatchHandler<TResult>> handlers) {
            this.handlers = handlers;
        }

        public List<PathDispatchHandler<TResult>> getHandlers() {
            return handlers;
        }
    }

    public static final class ActionPathDispatchConfig<TResult> {
        private final AntPathMatcher matcher;
        private final List<PathActionMatchRule<TResult, ?>> rules;

        public ActionPathDispatchConfig(AntPathMatcher matcher, List<PathActionMatchRule<TResult, ?>> rules) {
            this.matcher = matcher;
            this.rules = rules;
        }

        public AntPathMatcher getMatcher() {
            return matcher;
        }

        public List<PathActionMatchRule<TResult, ?>> getRules() {
            return rules;
        }
    }
}
