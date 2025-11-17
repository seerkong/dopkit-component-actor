package com.dopkit.dispatch;

/**
 * Base dispatch request carrying the requested strategy type.
 *
 * @param <TResult> result type
 */
public abstract class DispatchRequest<TResult> {
    private final DispatchStrategyType type;

    protected DispatchRequest(DispatchStrategyType type) {
        this.type = type;
    }

    public DispatchStrategyType getType() {
        return type;
    }
}
