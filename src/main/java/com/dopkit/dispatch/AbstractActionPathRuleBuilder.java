package com.dopkit.dispatch;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Shared builder base for action+path rules.
 */
abstract class AbstractActionPathRuleBuilder<TResult, TAction> {
    private final ActionPathRuleBuilder<TResult, TAction> delegate;

    AbstractActionPathRuleBuilder(String pattern) {
        this.delegate = ActionPathRuleBuilder.create(pattern);
    }

    public AbstractActionPathRuleBuilder<TResult, TAction> allowAll() {
        delegate.mode(ActionMatchMode.ALL);
        delegate.actions(null);
        return this;
    }

    public AbstractActionPathRuleBuilder<TResult, TAction> whitelist(Set<TAction> actions) {
        delegate.mode(ActionMatchMode.IN);
        delegate.actions(actions);
        return this;
    }

    public AbstractActionPathRuleBuilder<TResult, TAction> blacklist(Set<TAction> actions) {
        delegate.mode(ActionMatchMode.NOT_IN);
        delegate.actions(actions);
        return this;
    }

    public AbstractActionPathRuleBuilder<TResult, TAction> whitelist(@SuppressWarnings("unchecked") TAction... actions) {
        delegate.mode(ActionMatchMode.IN);
        delegate.actions(copyOf(actions));
        return this;
    }

    public AbstractActionPathRuleBuilder<TResult, TAction> blacklist(@SuppressWarnings("unchecked") TAction... actions) {
        delegate.mode(ActionMatchMode.NOT_IN);
        delegate.actions(copyOf(actions));
        return this;
    }

    public AbstractActionPathRuleBuilder<TResult, TAction> handler(PathActionRuleHandler<TResult> handler) {
        delegate.handler(handler);
        return this;
    }

    public PathActionMatchRule<TResult, TAction> build() {
        return delegate.build();
    }

    protected Set<TAction> copyOf(TAction... actions) {
        if (actions == null || actions.length == 0) {
            return null;
        }
        return new LinkedHashSet<>(Arrays.asList(actions));
    }
}
