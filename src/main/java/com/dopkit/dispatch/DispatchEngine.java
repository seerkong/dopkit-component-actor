package com.dopkit.dispatch;

import com.dopkit.dispatch.DispatchStrategyConfig.ActionPathDispatchConfig;
import com.dopkit.dispatch.DispatchStrategyConfig.ClassDispatchConfig;
import com.dopkit.dispatch.DispatchStrategyConfig.CommandDispatchConfig;
import com.dopkit.dispatch.DispatchStrategyConfig.EnumDispatchConfig;
import com.dopkit.dispatch.DispatchStrategyConfig.PathDispatchConfig;
import com.dopkit.dispatch.DispatchStrategyConfig.RouteKeyDispatchConfig;
import com.dopkit.dispatch.DispatchStrategyConfig.RouteKeyToEnumDispatchConfig;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Central dispatch engine with pluggable strategy configurations.
 */
public final class DispatchEngine<TResult> {
    private final Map<DispatchStrategyType, DispatchStrategyConfig<TResult>> strategies =
            new EnumMap<>(DispatchStrategyType.class);

    public DispatchEngine<TResult> registerStrategy(DispatchStrategyConfig<TResult> config) {
        if (config != null) {
            strategies.put(config.getType(), config);
        }
        return this;
    }

    public DispatchResult<TResult> dispatch(DispatchRequest<TResult> request) {
        if (request == null) {
            return DispatchResult.notHandled();
        }
        DispatchStrategyConfig<TResult> config = strategies.get(request.getType());
        if (config == null) {
            return DispatchResult.notHandled();
        }

        switch (request.getType()) {
            case CLASS:
                return dispatchClass((ClassDispatchRequest<TResult>) request, config.getClassConfig());
            case ROUTE_KEY:
                return dispatchRouteKey((RouteKeyDispatchRequest<TResult>) request, config.getRouteKeyConfig());
            case ENUM:
                return dispatchEnum((EnumDispatchRequest<TResult>) request, config.getEnumConfig());
            case ROUTE_KEY_TO_ENUM:
                return dispatchRouteKeyToEnum(
                        (RouteKeyToEnumDispatchRequest<TResult>) request,
                        config.getRouteKeyToEnumConfig());
            case COMMAND_TABLE:
                return dispatchCommand((CommandDispatchRequest<TResult>) request, config.getCommandDispatchConfig());
            case PATH:
                return dispatchPath((PathDispatchRequest<TResult>) request, config.getPathDispatchConfig());
            case ACTION_PATH:
                return dispatchActionPath(
                        (ActionPathDispatchRequest<TResult>) request,
                        config.getActionPathDispatchConfig());
            default:
                return DispatchResult.notHandled();
        }
    }

    private DispatchResult<TResult> dispatchClass(
            ClassDispatchRequest<TResult> request,
            ClassDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        Object input = request.getInput();
        Class<?> inputClass = input == null ? Void.class : input.getClass();
        Function<Object, TResult> handler = config.getHandlerMap().get(inputClass);
        if (handler != null) {
            return DispatchResult.handled(handler.apply(input));
        }
        if (config.getDefaultHandler() != null) {
            return DispatchResult.handled(config.getDefaultHandler().apply(input));
        }
        return DispatchResult.notHandled();
    }

    private DispatchResult<TResult> dispatchRouteKey(
            RouteKeyDispatchRequest<TResult> request,
            RouteKeyDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        Function<Object, TResult> handler = config.getHandlerMap().get(request.getRouteKey());
        if (handler != null) {
            return DispatchResult.handled(handler.apply(request.getInput()));
        }
        if (request.isApplyDefaultHandlers()) {
            if (config.getDefaultKeyHandler() != null) {
                return DispatchResult.handled(
                        config.getDefaultKeyHandler().apply(request.getRouteKey(), request.getInput()));
            }
            if (config.getDefaultInputHandler() != null) {
                return DispatchResult.handled(config.getDefaultInputHandler().apply(request.getInput()));
            }
        }
        return DispatchResult.notHandled();
    }

    private DispatchResult<TResult> dispatchEnum(
            EnumDispatchRequest<TResult> request,
            EnumDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        Function<Object, TResult> handler = config.getHandlerMap().get(request.getRouteEnum());
        if (handler != null) {
            return DispatchResult.handled(handler.apply(request.getInput()));
        }
        if (config.getDefaultEnumHandler() != null) {
            return DispatchResult.handled(
                    config.getDefaultEnumHandler().apply(request.getRouteEnum(), request.getInput()));
        }
        if (config.getDefaultInputHandler() != null) {
            return DispatchResult.handled(config.getDefaultInputHandler().apply(request.getInput()));
        }
        return DispatchResult.notHandled();
    }

    private DispatchResult<TResult> dispatchRouteKeyToEnum(
            RouteKeyToEnumDispatchRequest<TResult> request,
            RouteKeyToEnumDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        for (Function<String, ? extends Enum<?>> converter : config.getConverters().values()) {
            Enum<?> enumValue = converter.apply(request.getRouteKey());
            if (enumValue == null) {
                continue;
            }
            Function<Object, TResult> handler = config.getEnumHandlerMap().get(enumValue);
            if (handler != null) {
                return DispatchResult.handled(handler.apply(request.getInput()));
            }
        }
        return DispatchResult.notHandled();
    }

    private DispatchResult<TResult> dispatchCommand(
            CommandDispatchRequest<TResult> request,
            CommandDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        Function<String, ? extends Enum<?>> converter = config.getCommandConverter();
        Enum<?> commandEnum = converter != null ? converter.apply(request.getCommand()) : null;
        if (commandEnum != null && config.getHandlerExtractor() != null) {
            Function<Object, TResult> handler = config.getHandlerExtractor().apply(commandEnum);
            if (handler != null) {
                return DispatchResult.handled(handler.apply(request.getInput()));
            }
        }
        if (config.getDefaultHandler() != null) {
            return DispatchResult.handled(config.getDefaultHandler().apply(request.getCommand(), request.getInput()));
        }
        return DispatchResult.notHandled();
    }

    private DispatchResult<TResult> dispatchPath(
            PathDispatchRequest<TResult> request,
            PathDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        for (PathDispatchHandler<TResult> handler : config.getHandlers()) {
            TResult result = handler.tryHandle(request.getContext());
            if (result != null) {
                return DispatchResult.handled(result);
            }
        }
        return DispatchResult.notHandled();
    }

    private DispatchResult<TResult> dispatchActionPath(
            ActionPathDispatchRequest<TResult> request,
            ActionPathDispatchConfig<TResult> config) {
        if (config == null) {
            return DispatchResult.notHandled();
        }
        AntPathMatcher matcher = config.getMatcher();
        for (PathActionMatchRule<TResult, ?> rule : config.getRules()) {
            TResult result = rule.tryHandle(matcher, request.getContext());
            if (result != null) {
                return DispatchResult.handled(result);
            }
        }
        return DispatchResult.notHandled();
    }
}
