package com.dopkit.dispatch;

/**
 * Dispatch request for route-key routing.
 */
public final class RouteKeyDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final String routeKey;
    private final Object input;
    private final boolean applyDefaultHandlers;

    private RouteKeyDispatchRequest(String routeKey, Object input, boolean applyDefaultHandlers) {
        super(DispatchStrategyType.ROUTE_KEY);
        this.routeKey = routeKey;
        this.input = input;
        this.applyDefaultHandlers = applyDefaultHandlers;
    }

    public static <TResult> RouteKeyDispatchRequest<TResult> direct(String routeKey, Object input) {
        return new RouteKeyDispatchRequest<>(routeKey, input, false);
    }

    public static <TResult> RouteKeyDispatchRequest<TResult> withDefaults(String routeKey, Object input) {
        return new RouteKeyDispatchRequest<>(routeKey, input, true);
    }

    public String getRouteKey() {
        return routeKey;
    }

    public Object getInput() {
        return input;
    }

    public boolean isApplyDefaultHandlers() {
        return applyDefaultHandlers;
    }
}
