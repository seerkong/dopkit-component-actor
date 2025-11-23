package com.dopkit.router;

import com.dopkit.dispatch.ActionMatchMode;
import com.dopkit.dispatch.PathActionDispatchContext;
import com.dopkit.dispatch.ActionPathDispatchRequest;
import com.dopkit.dispatch.PathActionMatchRule;
import com.dopkit.dispatch.PathActionRuleHandler;
import com.dopkit.dispatch.AntPathMatcher;
import com.dopkit.dispatch.DispatchEngine;
import com.dopkit.dispatch.DispatchResult;
import com.dopkit.dispatch.DispatchStrategyConfig;
import com.dopkit.dispatch.PathMatchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Router that matches both path patterns and actions.
 */
public class GenericPathActionRouter
        <TRuntime, TRequest, TResponse, TAction> {

    private final Function<TRequest, String> pathExtractor;
    private final AntPathMatcher pathMatcher;
    private final List<PathActionMatchRule<TResponse, TAction>> rules;
    private final DispatchEngine<TResponse> dispatchEngine;

    public GenericPathActionRouter(Function<TRequest, String> pathExtractor) {
        this(pathExtractor, new AntPathMatcher());
    }

    public GenericPathActionRouter(Function<TRequest, String> pathExtractor, AntPathMatcher matcher) {
        this.pathExtractor = Objects.requireNonNull(pathExtractor, "pathExtractor");
        this.pathMatcher = matcher == null ? new AntPathMatcher() : matcher;
        this.rules = new ArrayList<>();
        this.dispatchEngine = new DispatchEngine<>();
        this.dispatchEngine.registerStrategy(
                DispatchStrategyConfig.forActionPathStrategy(
                        new DispatchStrategyConfig.ActionPathDispatchConfig<>(this.pathMatcher, (List) rules)));
    }

    public GenericPathActionRouter<TRuntime, TRequest, TResponse, TAction> registerPathAllActions(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        return register(pattern, handler, ActionMatchMode.ALL, null);
    }

    public GenericPathActionRouter<TRuntime, TRequest, TResponse, TAction> registerPathInActions(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler,
            Set<TAction> allowedActions) {
        return register(pattern, handler, ActionMatchMode.IN, allowedActions);
    }

    public GenericPathActionRouter<TRuntime, TRequest, TResponse, TAction> registerPathNotInActions(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler,
            Set<TAction> deniedActions) {
        return register(pattern, handler, ActionMatchMode.NOT_IN, deniedActions);
    }

    public GenericPathActionRouter<TRuntime, TRequest, TResponse, TAction> register(
            PathActionRoute<TRuntime, TRequest, TResponse, TAction> route) {
        if (route == null) {
            return this;
        }
        return register(route.getPattern(), route.getHandler(), route.getMode(), route.getActions());
    }

    public GenericPathActionRouter<TRuntime, TRequest, TResponse, TAction> registerAll(
            Collection<? extends PathActionRoute<TRuntime, TRequest, TResponse, TAction>> routeList) {
        if (routeList == null) {
            return this;
        }
        routeList.forEach(this::register);
        return this;
    }

    public GenericPathActionRouter<TRuntime, TRequest, TResponse, TAction> register(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler,
            ActionMatchMode mode,
            Set<TAction> actions) {
        Objects.requireNonNull(handler, "handler");
        PathActionRuleHandler<TResponse> ruleHandler = context -> {
            @SuppressWarnings("unchecked")
            TRuntime runtime = (TRuntime) context.getRuntime();
            @SuppressWarnings("unchecked")
            TRequest request = (TRequest) context.getRequest();
            return handler.handle(runtime, request, context.getPathMatchResult());
        };
        rules.add(new PathActionMatchRule<>(pattern, mode, actions, ruleHandler));
        return this;
    }

    public TResponse dispatch(TRuntime runtime, TRequest request, TAction action) {
        String path = pathExtractor.apply(request);
        PathActionDispatchContext<TRuntime, TRequest, TAction> context =
                new PathActionDispatchContext<>(runtime, request, action, path);
        DispatchResult<TResponse> result =
                dispatchEngine.dispatch(new ActionPathDispatchRequest<>(context));
        return result.isHandled() ? result.getResult() : null;
    }

    public int getRegistrationCount() {
        return rules.size();
    }
}
