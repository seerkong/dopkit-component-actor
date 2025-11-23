package com.dopkit.dispatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumActionPathRuleBuilderTest {

    enum HttpMethod {GET, POST, DELETE}

    @Test
    void buildsEnumRules() {
        PathActionMatchRule<String, HttpMethod> rule = EnumActionPathRuleBuilder
                .<String, HttpMethod>create("/enum/**")
                .whitelist(HttpMethod.GET, HttpMethod.POST)
                .handler(ctx -> "enum-ok")
                .build();

        DispatchEngine<String> engine = new DispatchEngine<>();
        engine.registerStrategy(
                DispatchStrategyConfig.forActionPathStrategy(
                        new DispatchStrategyConfig.ActionPathDispatchConfig<>(
                                new AntPathMatcher(),
                                java.util.Collections.singletonList(rule))));

        DispatchResult<String> result = engine.dispatch(new ActionPathDispatchRequest<>(
                new PathActionDispatchContext<>(null, null, HttpMethod.GET, "/enum/resource")) );
        assertTrue(result.isHandled());
        assertEquals("enum-ok", result.getResult());
    }
}
