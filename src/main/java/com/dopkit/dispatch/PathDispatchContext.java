package com.dopkit.dispatch;

/**
 * Context passed to path-based dispatch handlers.
 *
 * @param <TRuntime> runtime type
 * @param <TRequest> request type
 */
public final class PathDispatchContext<TRuntime, TRequest> {
    private final TRuntime runtime;
    private final TRequest request;

    public PathDispatchContext(TRuntime runtime, TRequest request) {
        this.runtime = runtime;
        this.request = request;
    }

    public TRuntime getRuntime() {
        return runtime;
    }

    public TRequest getRequest() {
        return request;
    }
}
