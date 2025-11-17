package com.dopkit.dispatch;

/**
 * Dispatch request for the ACTION_PATH strategy.
 */
public final class ActionPathDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final ActionPathDispatchContext<?, ?, ?> context;

    public ActionPathDispatchRequest(ActionPathDispatchContext<?, ?, ?> context) {
        super(DispatchStrategyType.ACTION_PATH);
        this.context = context;
    }

    public ActionPathDispatchContext<?, ?, ?> getContext() {
        return context;
    }
}
