## Overview

AbstractActor currently hardcodes five dispatch mechanisms (class, route key, enum, route key→enum, command table) and GenericRouter embeds a path matcher. Adding a new dispatch mode such as HTTP method + path requires editing both call sites. We need a reusable dispatch engine that centralizes strategy selection, configuration, and execution so future strategies can be plugged in without duplicating logic.

## Goals
- Extract the shared routing/dispatch mechanics into a configurable engine that both `AbstractActor` and `GenericRouter` can use.
- Introduce an enum-backed strategy registry plus a configuration object that carries type-specific properties for each strategy.
- Preserve existing behaviors of all five Actor strategies and the path matcher in `GenericRouter`, verified by the current test suite.
- Provide extension points so a forthcoming change can add an HTTP method + path strategy without modifying the core classes again.

## Non-Goals
- Implement the HTTP method + path strategy (that will be done after this refactor).
- Change public API semantics beyond introducing the dispatch engine wrappers required to configure strategies.
- Optimize performance or concurrency—focus on architecture and extensibility first.

## Success Criteria
- New dispatch engine API exists with strategy enum names and structured strategy-specific configs.
- `AbstractActor` and `GenericRouter` depend on the engine rather than maintaining bespoke routing code.
- Tests covering actor, command table, router path matching, and adapter flows keep passing.
- Documentation/comments explain how to register existing strategies using the new engine.
