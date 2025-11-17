package com.dopkit.router;

import com.dopkit.dispatch.PathMatchResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GenericPathRouterTest {

    @Test
    void matchesPathsUsingAntPatterns() {
        GenericPathRouter<String, TestRequest, String> router =
                new GenericPathRouter<>(TestRequest::getPath);
        router.register("/users/*", handler("users"));
        router.register("/admin/**", handler("admin"));

        assertEquals("users", router.dispatch("rt", new TestRequest("/users/1")));
        assertEquals("admin", router.dispatch("rt", new TestRequest("/admin/logs/today")));
        assertNull(router.dispatch("rt", new TestRequest("/unknown")));
    }

    private ComponentHandler<String, TestRequest, PathMatchResult, String> handler(String value) {
        return (runtime, request, matchResult) -> value;
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
