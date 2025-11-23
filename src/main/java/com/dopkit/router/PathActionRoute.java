package com.dopkit.router;

import com.dopkit.dispatch.ActionMatchMode;
import com.dopkit.dispatch.PathMatchResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable route definition that can be registered to {@link GenericPathActionRouter}
 * or its subclasses using a static list + aggregation style.
 */
public class PathActionRoute<TRuntime, TRequest, TResponse, TAction> {
    private final String pattern;
    private final ActionMatchMode mode;
    private final Set<TAction> actions;
    private final ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler;

    private PathActionRoute(
            String pattern,
            ActionMatchMode mode,
            Set<TAction> actions,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        this.pattern = Objects.requireNonNull(pattern, "pattern");
        this.mode = Objects.requireNonNull(mode, "mode");
        this.handler = Objects.requireNonNull(handler, "handler");
        this.actions = actions == null ? null : Collections.unmodifiableSet(new HashSet<>(actions));
    }

    public static <TRuntime, TRequest, TResponse, TAction>
    PathActionRoute<TRuntime, TRequest, TResponse, TAction> pathAllAction(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        return new PathActionRoute<>(pattern, ActionMatchMode.ALL, null, handler);
    }

    public static <TRuntime, TRequest, TResponse, TAction>
    PathActionRoute<TRuntime, TRequest, TResponse, TAction> pathInAction(
            String pattern,
            Collection<TAction> actions,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        return new PathActionRoute<>(pattern, ActionMatchMode.IN, new HashSet<>(actions), handler);
    }

    public static <TRuntime, TRequest, TResponse, TAction>
    PathActionRoute<TRuntime, TRequest, TResponse, TAction> pathNotInAction(
            String pattern,
            Collection<TAction> actions,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        return new PathActionRoute<>(pattern, ActionMatchMode.NOT_IN, new HashSet<>(actions), handler);
    }

    public String getPattern() {
        return pattern;
    }

    public ActionMatchMode getMode() {
        return mode;
    }

    public Set<TAction> getActions() {
        return actions;
    }

    public ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> getHandler() {
        return handler;
    }
}
