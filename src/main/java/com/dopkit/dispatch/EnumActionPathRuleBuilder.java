package com.dopkit.dispatch;

import java.util.EnumSet;
import java.util.Set;

/**
 * Convenience builder for enum-based action path rules.
 */
public class EnumActionPathRuleBuilder<TResult, TAction extends Enum<TAction>>
        extends AbstractActionPathRuleBuilder<TResult, TAction> {

    private EnumActionPathRuleBuilder(String pattern) {
        super(pattern);
    }

    public static <TResult, TAction extends Enum<TAction>> EnumActionPathRuleBuilder<TResult, TAction> create(
            String pattern) {
        return new EnumActionPathRuleBuilder<>(pattern);
    }

    @SafeVarargs
    @Override
    public final EnumActionPathRuleBuilder<TResult, TAction> whitelist(TAction... actions) {
        super.whitelist(actions);
        return this;
    }

    @Override
    public EnumActionPathRuleBuilder<TResult, TAction> whitelist(Set<TAction> actions) {
        super.whitelist(actions);
        return this;
    }

    @SafeVarargs
    @Override
    public final EnumActionPathRuleBuilder<TResult, TAction> blacklist(TAction... actions) {
        super.blacklist(actions);
        return this;
    }

    @Override
    public EnumActionPathRuleBuilder<TResult, TAction> blacklist(Set<TAction> actions) {
        super.blacklist(actions);
        return this;
    }

    @Override
    public EnumActionPathRuleBuilder<TResult, TAction> allowAll() {
        super.allowAll();
        return this;
    }

    @Override
    public EnumActionPathRuleBuilder<TResult, TAction> handler(PathActionRuleHandler<TResult> handler) {
        super.handler(handler);
        return this;
    }

    @Override
    public PathActionMatchRule<TResult, TAction> build() {
        return super.build();
    }

    @Override
    protected Set<TAction> copyOf(TAction... actions) {
        if (actions == null || actions.length == 0) {
            return null;
        }
        return EnumSet.copyOf(java.util.Arrays.asList(actions));
    }
}
