package com.dopkit.dispatch;

@FunctionalInterface
public interface PathActionRuleHandler<TResult> {
    TResult handle(PathActionDispatchContext<?, ?, ?> context);
}
