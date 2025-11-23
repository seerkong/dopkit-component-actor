package com.dopkit.dispatch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Rule describing how to match a path pattern and action prior to invoking a handler.
 */
public final class PathActionMatchRule<TResult, TAction> {
    private final String pattern;
    private final ActionMatchMode mode;
    private final Set<TAction> actions;
    private final PathActionRuleHandler<TResult> handler;

    public PathActionMatchRule(
            String pattern,
            ActionMatchMode mode,
            Set<TAction> actions,
            PathActionRuleHandler<TResult> handler) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
        this.pattern = pattern;
        this.mode = mode == null ? ActionMatchMode.ALL : mode;
        this.handler = handler;
        if (this.mode == ActionMatchMode.ALL) {
            this.actions = null;
        } else {
            if (actions == null || actions.isEmpty()) {
                throw new IllegalArgumentException("actions must not be empty for mode " + mode);
            }
            this.actions = Collections.unmodifiableSet(new HashSet<>(actions));
        }
    }

    public TResult tryHandle(
            AntPathMatcher matcher,
            PathActionDispatchContext<?, ?, ?> context) {
        if (!matcher.match(pattern, context.getPath())) {
            return null;
        }
        if (!actionMatches(context.getAction())) {
            return null;
        }
        PathMatchResult matchResult = matcher.matchAndExtract(pattern, context.getPath());
        return handler.handle(context.withMatchResult(matchResult));
    }

    private boolean actionMatches(Object action) {
        switch (mode) {
            case ALL:
                return true;
            case IN:
                return actions.contains(action);
            case NOT_IN:
                return !actions.contains(action);
            default:
                throw new IllegalStateException("Unsupported match mode: " + mode);
        }
    }

    public String getPattern() {
        return pattern;
    }

    public ActionMatchMode getMode() {
        return mode;
    }

    public Set<TAction> getActions() {
        return actions;
    }

    public PathActionRuleHandler<TResult> getHandler() {
        return handler;
    }
}
