package com.dopkit.router;

import com.dopkit.dispatch.ActionMatchMode;
import com.dopkit.dispatch.PathMatchResult;

import java.util.Objects;
import java.util.function.Function;

/**
 * Path-only router backed by {@link GenericPathActionRouter} with ALL-action mode.
 */
public class GenericPathRouter<TRuntime, TRequest, TResponse>
        extends GenericPathActionRouter<TRuntime, TRequest, TResponse, Void> {

    public GenericPathRouter(Function<TRequest, String> pathExtractor) {
        super(pathExtractor);
    }

    public GenericPathRouter(Function<TRequest, String> pathExtractor, com.dopkit.dispatch.AntPathMatcher matcher) {
        super(pathExtractor, matcher);
    }

    public GenericPathRouter<TRuntime, TRequest, TResponse> register(
            String pattern,
            ComponentHandler<TRuntime, TRequest, PathMatchResult, TResponse> handler) {
        Objects.requireNonNull(handler, "handler");
        super.register(pattern, handler, ActionMatchMode.ALL, null);
        return this;
    }

    public TResponse dispatch(TRuntime runtime, TRequest request) {
        return super.dispatch(runtime, request, null);
    }
}
