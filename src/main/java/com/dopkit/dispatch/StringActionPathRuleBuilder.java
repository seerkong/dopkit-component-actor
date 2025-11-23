package com.dopkit.dispatch;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Convenience builder for string-based action path rules, reusing the same core logic as enum builders.
 */
public final class StringActionPathRuleBuilder<TResult>
        extends AbstractActionPathRuleBuilder<TResult, String> {

    private StringActionPathRuleBuilder(String pattern) {
        super(pattern);
    }

    public static <TResult> StringActionPathRuleBuilder<TResult> create(String pattern) {
        return new StringActionPathRuleBuilder<>(pattern);
    }

    @Override
    public StringActionPathRuleBuilder<TResult> allowAll() {
        super.allowAll();
        return this;
    }

    public StringActionPathRuleBuilder<TResult> whitelist(String... actions) {
        super.whitelist(actions);
        return this;
    }

    public StringActionPathRuleBuilder<TResult> blacklist(String... actions) {
        super.blacklist(actions);
        return this;
    }

    @Override
    public StringActionPathRuleBuilder<TResult> whitelist(Set<String> actions) {
        super.whitelist(actions);
        return this;
    }

    @Override
    public StringActionPathRuleBuilder<TResult> blacklist(Set<String> actions) {
        super.blacklist(actions);
        return this;
    }

    @Override
    public StringActionPathRuleBuilder<TResult> handler(PathActionRuleHandler<TResult> handler) {
        super.handler(handler);
        return this;
    }

    @Override
    public PathActionMatchRule<TResult, String> build() {
        return super.build();
    }

    @Override
    protected Set<String> copyOf(String... actions) {
        if (actions == null || actions.length == 0) {
            return null;
        }
        return new LinkedHashSet<>(Arrays.asList(actions));
    }
}
