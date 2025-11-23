package com.dopkit.dispatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ActionPathRuleBuilderTest {

    @Test
    void buildsGenericRule() {
        PathActionMatchRule<String, Integer> rule = ActionPathRuleBuilder
                .<String, Integer>create("/reports/**")
                .mode(ActionMatchMode.NOT_IN)
                .actions(java.util.Collections.singleton(1))
                .handler(ctx -> "ok")
                .build();

        DispatchEngine<String> engine = new DispatchEngine<>();
        engine.registerStrategy(
                DispatchStrategyConfig.forActionPathStrategy(
                        new DispatchStrategyConfig.ActionPathDispatchConfig<>(
                                new AntPathMatcher(),
                                java.util.Collections.singletonList(rule))));

        com.dopkit.dispatch.DispatchResult<String> result = engine.dispatch(new ActionPathDispatchRequest<>(
                new PathActionDispatchContext<>(null, null, 2, "/reports/today")));
        assert result.isHandled();
        assertEquals("ok", result.getResult());
    }

    @Test
    void stringBuilderConvenienceMethods() {
        PathActionMatchRule<String, String> rule = StringActionPathRuleBuilder.<String>create("/metrics/**")
                .whitelist("GET")
                .handler(ctx -> "string")
                .build();
        assertNotNull(rule);
    }
}
