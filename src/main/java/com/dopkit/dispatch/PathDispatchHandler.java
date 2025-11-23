package com.dopkit.dispatch;

/**
 * Handler entry for path-based dispatching.
 *
 * @param <TResult> response type
 */
@FunctionalInterface
public interface PathDispatchHandler<TResult> {
    /**
     * Try to handle the given context.
     *
     * @param context runtime/request context
     * @return response when handled, or {@code null} to continue probing.
     */
    TResult tryHandle(PathDispatchContext<?, ?> context);
}
