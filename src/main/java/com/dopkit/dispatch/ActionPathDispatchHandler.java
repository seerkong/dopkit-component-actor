package com.dopkit.dispatch;

/**
 * Handler entry evaluated by the ACTION_PATH dispatch strategy.
 *
 * @param <TResult> result type
 */
@FunctionalInterface
public interface ActionPathDispatchHandler<TResult> {
    /**
     * Try handling the given context.
     *
     * @param context runtime/request/action context
     * @return handler result when handled; {@code null} otherwise
     */
    TResult tryHandle(PathActionDispatchContext<?, ?, ?> context);
}
