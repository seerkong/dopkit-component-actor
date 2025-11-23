package com.dopkit.dispatch;

import com.dopkit.actor.AbstractActor;
import com.dopkit.actor.ActorRoute;
import com.dopkit.actor.ActorRouteBuilder;
import com.dopkit.router.GenericPathRouter;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DispatchEngineIntegrationTest {

    private final EngineBackedActor actor = new EngineBackedActor();

    @Test
    void actorDispatchesByClassThroughEngine() {
        String result = actor.call("hello");
        assertEquals("class:hello", result);
    }

    @Test
    void actorRouteKeyFallsBackToEnumBeforeDefault() {
        String viaEnum = actor.callByEnum(TestEnum.STRING, "payload");
        String viaRouteKey = actor.callByRouteKey("STRING", "payload");
        assertEquals(viaEnum, viaRouteKey);
    }

    @Test
    void actorCommandTableUsesDefaultHandlerWhenMissing() {
        String result = actor.callByCommand("UNKNOWN_CMD", "body");
        assertEquals("command-default:UNKNOWN_CMD", result);
    }

    @Test
    void genericRouterDispatchesViaPathStrategy() {
        GenericPathRouter<String, TestRequest, String> router =
                new GenericPathRouter<>(TestRequest::getPath);
        router.register("/ping", (runtime, request, match) -> runtime + ":" + match.getPath());

        assertEquals("rt:/ping", router.dispatch("rt", new TestRequest("/ping")));
        assertNull(router.dispatch("rt", new TestRequest("/other")));
    }

    private static final class EngineBackedActor extends AbstractActor<String> {
        private final Map<TestCommand, String> commandOutputs = new EnumMap<>(TestCommand.class);

        private EngineBackedActor() {
            commandOutputs.put(TestCommand.PING, "command:PING");
        }

        @Override
        protected ActorRoute<String> createActorRoute() {
            return ActorRouteBuilder.<String>create()
                    .match(String.class,
                            DispatchTestSets.routeKeys("STRING_DIRECT"),
                            DispatchTestSets.enums(TestEnum.STRING),
                            value -> "class:" + value)
                    .match(Integer.class,
                            DispatchTestSets.routeKeys("INT_DIRECT"),
                            null,
                            value -> "int:" + value)
                    .registerEnumConverter(TestEnum.class, key -> {
                        try {
                            return TestEnum.valueOf(key.toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            return null;
                        }
                    })
                    .matchAny(input -> "default-input:" + input)
                    .matchAnyKey((key, input) -> "default-key:" + key)
                    .matchAnyEnum((routeEnum, input) -> "default-enum:" + routeEnum)
                    .registerCommandTable(
                            command -> {
                                try {
                                    return TestCommand.valueOf(command);
                                } catch (IllegalArgumentException ex) {
                                    return null;
                                }
                            },
                            cmd -> input -> commandOutputs.get(cmd),
                            (cmd, input) -> "command-default:" + cmd)
                    .build();
        }

        @Override
        protected String createErrorResult(String message) {
            return "error:" + message;
        }
    }

    private enum TestEnum {
        STRING
    }

    private enum TestCommand {
        PING
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

    private static final class DispatchTestSets {
        private DispatchTestSets() {
        }

        static <T> java.util.Set<T> routeKeys(T value) {
            java.util.Set<T> set = new java.util.HashSet<>();
            set.add(value);
            return set;
        }

        static <E extends Enum<E>> java.util.Set<E> enums(E value) {
            java.util.Set<E> set = new java.util.HashSet<>();
            set.add(value);
            return set;
        }
    }
}
