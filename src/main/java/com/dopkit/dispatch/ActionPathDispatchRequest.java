package com.dopkit.dispatch;

/**
 * Dispatch request for the ACTION_PATH strategy.
 */
public final class ActionPathDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final PathActionDispatchContext<?, ?, ?> context;

    public ActionPathDispatchRequest(PathActionDispatchContext<?, ?, ?> context) {
        super(DispatchStrategyType.ACTION_PATH);
        this.context = context;
    }

    public PathActionDispatchContext<?, ?, ?> getContext() {
        return context;
    }
}
