package com.dopkit.dispatch;

import java.util.Objects;
import java.util.Set;

/**
 * Fluent builder for creating {@link PathActionMatchRule} instances.
 */
public final class ActionPathRuleBuilder<TResult, TAction> {
    private final String pattern;
    private ActionMatchMode mode = ActionMatchMode.ALL;
    private Set<TAction> actions;
    private PathActionRuleHandler<TResult> handler;

    private ActionPathRuleBuilder(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
        this.pattern = pattern;
    }

    public static <TResult, TAction> ActionPathRuleBuilder<TResult, TAction> create(String pattern) {
        return new ActionPathRuleBuilder<>(pattern);
    }

    public ActionPathRuleBuilder<TResult, TAction> mode(ActionMatchMode mode) {
        this.mode = mode == null ? ActionMatchMode.ALL : mode;
        return this;
    }

    public ActionPathRuleBuilder<TResult, TAction> actions(Set<TAction> actions) {
        this.actions = actions;
        return this;
    }

    public ActionPathRuleBuilder<TResult, TAction> handler(PathActionRuleHandler<TResult> handler) {
        this.handler = handler;
        return this;
    }

    public PathActionMatchRule<TResult, TAction> build() {
        Objects.requireNonNull(handler, "handler");
        return new PathActionMatchRule<>(pattern, mode, actions, handler);
    }
}
