package com.dopkit.dispatch;

/**
 * Dispatch request for path-based routing.
 */
public final class PathDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final PathDispatchContext<?, ?> context;

    public PathDispatchRequest(PathDispatchContext<?, ?> context) {
        super(DispatchStrategyType.PATH);
        this.context = context;
    }

    public PathDispatchContext<?, ?> getContext() {
        return context;
    }
}
