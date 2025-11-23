package com.dopkit.dispatch;

/**
 * Context passed to action+path dispatch handlers.
 *
 * @param <TRuntime> runtime context type
 * @param <TRequest> request type
 * @param <TAction> action type
 */
public final class PathActionDispatchContext<TRuntime, TRequest, TAction> {
    private final TRuntime runtime;
    private final TRequest request;
    private final TAction action;
    private final String path;
    private final PathMatchResult pathMatchResult;

    public PathActionDispatchContext(TRuntime runtime, TRequest request, TAction action, String path) {
        this(runtime, request, action, path, null);
    }

    private PathActionDispatchContext(
            TRuntime runtime,
            TRequest request,
            TAction action,
            String path,
            PathMatchResult pathMatchResult) {
        this.runtime = runtime;
        this.request = request;
        this.action = action;
        this.path = path;
        this.pathMatchResult = pathMatchResult;
    }

    public TRuntime getRuntime() {
        return runtime;
    }

    public TRequest getRequest() {
        return request;
    }

    public TAction getAction() {
        return action;
    }

    public String getPath() {
        return path;
    }

    public PathMatchResult getPathMatchResult() {
        return pathMatchResult;
    }

    public PathActionDispatchContext<TRuntime, TRequest, TAction> withMatchResult(PathMatchResult result) {
        return new PathActionDispatchContext<>(runtime, request, action, path, result);
    }
}
