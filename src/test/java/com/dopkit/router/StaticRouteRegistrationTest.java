package com.dopkit.router;

import com.dopkit.example.ApiRequest;
import com.google.common.collect.Lists;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StaticRouteRegistrationTest {

    enum HttpAction {GET, POST, DELETE}

    static class MessagingRouteRegistry {
        static final List<PathActionRoute<String, ApiRequest, String, HttpAction>> routes = Lists.newArrayList(
                PathActionRoute.pathAllAction(
                        "/api/tenant/{tenantId}/stage/{stage}/ws/connect",
                        (runtime, request, match) -> "connect:" + match.getVariables().get("tenantId")),
                PathActionRoute.pathAllAction(
                        "/api/tenant/{tenantId}/stage/{stage}/ws/subscriptions",
                        (runtime, request, match) -> "subscriptions:" + match.getVariables().get("stage"))
        );
    }

    static class ProfileRouteRegistry {
        static final List<PathActionRoute<String, ApiRequest, String, HttpAction>> routes = Lists.newArrayList(
                PathActionRoute.pathInAction(
                        "/api/tenant/{tenantId}/stage/{stage}/profile/switch",
                        EnumSet.of(HttpAction.POST),
                        (runtime, request, match) -> "profileSwitch:" + match.getVariables().get("stage")),
                PathActionRoute.pathNotInAction(
                        "/api/tenant/{tenantId}/stage/{stage}/profile/archived",
                        EnumSet.of(HttpAction.DELETE),
                        (runtime, request, match) -> "profileArchived")
        );
    }

    private static final List<PathActionRoute<String, ApiRequest, String, HttpAction>> aggregatedRoutes =
            Stream.of(
                            MessagingRouteRegistry.routes,
                            ProfileRouteRegistry.routes)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

    @Test
    void shouldDispatchUsingStaticAggregatedRouteDefinitions() {
        GenericPathActionRouter<String, ApiRequest, String, HttpAction> router =
                new GenericPathActionRouter<>(ApiRequest::getPath);
        router.registerAll(aggregatedRoutes);

        ApiRequest connectReq = ApiRequest.builder()
                .path("/api/tenant/acme/stage/test/ws/connect")
                .build();
        assertEquals("connect:acme", router.dispatch("rt", connectReq, HttpAction.GET));

        ApiRequest subscriptionsReq = ApiRequest.builder()
                .path("/api/tenant/blue/stage/prod/ws/subscriptions")
                .build();
        assertEquals("subscriptions:prod", router.dispatch("rt", subscriptionsReq, HttpAction.POST));

        ApiRequest switchReq = ApiRequest.builder()
                .path("/api/tenant/acme/stage/dev/profile/switch")
                .build();
        assertEquals("profileSwitch:dev", router.dispatch("rt", switchReq, HttpAction.POST));

        ApiRequest blockedReq = ApiRequest.builder()
                .path("/api/tenant/acme/stage/dev/profile/archived")
                .build();
        assertEquals("profileArchived", router.dispatch("rt", blockedReq, HttpAction.GET));

        // Should not match when action not allowed
        assertNull(router.dispatch("rt", blockedReq, HttpAction.DELETE));
        assertNull(router.dispatch("rt", switchReq, HttpAction.GET));
    }
}
