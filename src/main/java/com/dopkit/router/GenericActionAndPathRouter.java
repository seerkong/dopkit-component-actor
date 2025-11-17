package com.dopkit.router;

import com.dopkit.dispatch.ActionMatchMode;
import com.dopkit.dispatch.ActionPathDispatchContext;
import com.dopkit.dispatch.ActionPathDispatchRequest;
import com.dopkit.dispatch.ActionPathMatchRule;
import com.dopkit.dispatch.ActionPathRuleHandler;
import com.dopkit.dispatch.AntPathMatcher;
import com.dopkit.dispatch.DispatchEngine;
import com.dopkit.dispatch.DispatchResult;
import com.dopkit.dispatch.DispatchStrategyConfig;
import com.dopkit.dispatch.PathMatchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Router that matches both path patterns and actions.
 */
public class GenericActionAndPathRouter
        <TRuntime, TRequest, TResponse, TAction> {

    private final Function<TRequest, String> pathExtractor;
    private final AntPathMatcher pathMatcher;
    private final List<ActionPathMatchRule<TResponse, TAction>> rules;
    private final DispatchEngine<TResponse> dispatchEngine;

    public GenericActionAndPathRouter(Function<TRequest, String> pathExtractor) {
        this(pathExtractor, new AntPathMatcher());
    }

    public GenericActionAndPathRouter(Function<TRequest, String> pathExtractor, AntPathMatcher matcher) {
        this.pathExtractor = Objects.requireNonNull(pathExtractor, "pathExtractor");
        this.pathMatcher = matcher == null ? new AntPathMatcher() : matcher;
        this.rules = new ArrayList<>();
        this.dispatchEngine = new DispatchEngine<>();
        this.dispatchEngine.registerStrategy(
                DispatchStrategyConfig.forActionPathStrategy(
                        new DispatchStrategyConfig.ActionPathDispatchConfig<>(this.pathMatcher, (List) rules)));
    }

    public GenericActionAndPathRouter<TRuntime, TRequest, TResponse, TAction> registerAllActions(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        return register(pattern, handler, ActionMatchMode.ALL, null);
    }

    public GenericActionAndPathRouter<TRuntime, TRequest, TResponse, TAction> registerWhitelist(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler,
            Set<TAction> allowedActions) {
        return register(pattern, handler, ActionMatchMode.WHITELIST, allowedActions);
    }

    public GenericActionAndPathRouter<TRuntime, TRequest, TResponse, TAction> registerBlacklist(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler,
            Set<TAction> deniedActions) {
        return register(pattern, handler, ActionMatchMode.BLACKLIST, deniedActions);
    }

    public GenericActionAndPathRouter<TRuntime, TRequest, TResponse, TAction> register(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler,
            ActionMatchMode mode,
            Set<TAction> actions) {
        Objects.requireNonNull(handler, "handler");
        ActionPathRuleHandler<TResponse> ruleHandler = context -> {
            @SuppressWarnings("unchecked")
            TRuntime runtime = (TRuntime) context.getRuntime();
            @SuppressWarnings("unchecked")
            TRequest request = (TRequest) context.getRequest();
            return handler.handle(runtime, request, context.getPathMatchResult());
        };
        rules.add(new ActionPathMatchRule<>(pattern, mode, actions, ruleHandler));
        return this;
    }

    public TResponse dispatch(TRuntime runtime, TRequest request, TAction action) {
        String path = pathExtractor.apply(request);
        ActionPathDispatchContext<TRuntime, TRequest, TAction> context =
                new ActionPathDispatchContext<>(runtime, request, action, path);
        DispatchResult<TResponse> result =
                dispatchEngine.dispatch(new ActionPathDispatchRequest<>(context));
        return result.isHandled() ? result.getResult() : null;
    }

    public int getRegistrationCount() {
        return rules.size();
    }
}
