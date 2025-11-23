package com.dopkit.router;

import com.dopkit.dispatch.ActionMatchMode;
import com.dopkit.dispatch.PathMatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenericPathActionRouterTest {

    private GenericPathActionRouter<String, TestRequest, String, String> router;

    @BeforeEach
    void setUp() {
        router = new GenericPathActionRouter<>(TestRequest::getPath);
    }

    @Test
    void dispatchAllModeIgnoresAction() {
        router.registerPathAllActions("/ping", handler("all"));

        assertEquals("all", router.dispatch("rt", new TestRequest("/ping"), "GET"));
        assertEquals("all", router.dispatch("rt", new TestRequest("/ping"), null));
    }

    @Test
    void dispatchWhitelistOnlyAllowsConfiguredActions() {
        router.registerPathInActions(
                "/users/**",
                handler("allowed"),
                setOf("GET", "POST"));

        assertEquals("allowed", router.dispatch("rt", new TestRequest("/users/1"), "GET"));
        assertNull(router.dispatch("rt", new TestRequest("/users"), "DELETE"));
    }

    @Test
    void dispatchBlacklistRejectsDeniedActions() {
        router.registerPathNotInActions(
                "/admin/**",
                handler("admin"),
                setOf("DELETE"));

        assertNull(router.dispatch("rt", new TestRequest("/admin/1"), "DELETE"));
        assertEquals("admin", router.dispatch("rt", new TestRequest("/admin/1"), "PATCH"));
    }

    @Test
    void registerWithoutActionsForWhitelistThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                router.register("/users", handler("noop"), ActionMatchMode.IN, null));
    }

    private ComponentHandler<String, TestRequest, PathMatchResult, String> handler(String value) {
        return (runtime, request, matchResult) -> value;
    }

    private Set<String> setOf(String... actions) {
        Set<String> set = new HashSet<>();
        for (String action : actions) {
            set.add(action);
        }
        return set;
    }

    private static final class TestRequest {
        private final String path;

        private TestRequest(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
