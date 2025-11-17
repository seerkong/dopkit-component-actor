package com.dopkit.dispatch;

@FunctionalInterface
public interface ActionPathRuleHandler<TResult> {
    TResult handle(ActionPathDispatchContext<?, ?, ?> context);
}
