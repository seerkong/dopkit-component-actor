package com.dopkit.dispatch;

/**
 * Result returned by the dispatch engine.
 *
 * @param <TResult> result type
 */
public final class DispatchResult<TResult> {
    private static final DispatchResult<?> NOT_HANDLED = new DispatchResult<>(false, null);

    private final boolean handled;
    private final TResult result;

    private DispatchResult(boolean handled, TResult result) {
        this.handled = handled;
        this.result = result;
    }

    public static <TResult> DispatchResult<TResult> handled(TResult result) {
        return new DispatchResult<>(true, result);
    }

    @SuppressWarnings("unchecked")
    public static <TResult> DispatchResult<TResult> notHandled() {
        return (DispatchResult<TResult>) NOT_HANDLED;
    }

    public boolean isHandled() {
        return handled;
    }

    public TResult getResult() {
        return result;
    }
}
