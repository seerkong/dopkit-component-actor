package com.dopkit.dispatch;

import com.dopkit.dispatch.DispatchStrategyConfig.ActionPathDispatchConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ActionPathDispatchEngineTest {

    @Test
    void matchesAllWhitelistAndBlacklistModes() {
        DispatchEngine<String> engine = new DispatchEngine<>();
        List<ActionPathMatchRule<String, ?>> rules = Arrays.asList(
                new ActionPathMatchRule<>(
                        "/health",
                        ActionMatchMode.ALL,
                        null,
                        context -> "all"),
                new ActionPathMatchRule<>(
                        "/users/**",
                        ActionMatchMode.WHITELIST,
                        new java.util.HashSet<>(Arrays.asList("GET", "POST")),
                        context -> "allowed"),
                new ActionPathMatchRule<>(
                        "/admin/**",
                        ActionMatchMode.BLACKLIST,
                        new java.util.HashSet<>(Arrays.asList("DELETE")),
                        context -> "safe")
        );

        engine.registerStrategy(
                DispatchStrategyConfig.forActionPathStrategy(
                        new ActionPathDispatchConfig<>(new AntPathMatcher(), rules)));

        assertEquals("all", dispatch(engine, "/health", "ANY"));
        assertEquals("allowed", dispatch(engine, "/users/1", "GET"));
        assertEquals("safe", dispatch(engine, "/admin/audit", "PATCH"));
        assertNull(dispatch(engine, "/users/1", "DELETE"));
    }

    private String dispatch(DispatchEngine<String> engine, String path, String action) {
        ActionPathDispatchContext<Void, Void, String> context =
                new ActionPathDispatchContext<>(null, null, action, path);
        DispatchResult<String> result =
                engine.dispatch(new ActionPathDispatchRequest<>(context));
        return result.isHandled() ? result.getResult() : null;
    }
}
