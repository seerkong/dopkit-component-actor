package com.dopkit.dispatch;

/**
 * Dispatch request for route-key-to-enum routing.
 */
public final class RouteKeyToEnumDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final String routeKey;
    private final Object input;

    public RouteKeyToEnumDispatchRequest(String routeKey, Object input) {
        super(DispatchStrategyType.ROUTE_KEY_TO_ENUM);
        this.routeKey = routeKey;
        this.input = input;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public Object getInput() {
        return input;
    }
}
