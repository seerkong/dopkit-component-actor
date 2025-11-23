package com.dopkit.dispatch;

/**
 * Dispatch request for class-based routing.
 */
public final class ClassDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final Object input;

    public ClassDispatchRequest(Object input) {
        super(DispatchStrategyType.CLASS);
        this.input = input;
    }

    public Object getInput() {
        return input;
    }
}
