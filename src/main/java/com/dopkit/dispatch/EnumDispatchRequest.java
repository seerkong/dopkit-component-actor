package com.dopkit.dispatch;

/**
 * Dispatch request for enum-based routing.
 */
public final class EnumDispatchRequest<TResult> extends DispatchRequest<TResult> {
    private final Enum<?> routeEnum;
    private final Object input;

    public EnumDispatchRequest(Enum<?> routeEnum, Object input) {
        super(DispatchStrategyType.ENUM);
        this.routeEnum = routeEnum;
        this.input = input;
    }

    public Enum<?> getRouteEnum() {
        return routeEnum;
    }

    public Object getInput() {
        return input;
    }
}
