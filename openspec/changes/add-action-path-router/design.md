## Context
Routing currently supports:
- `GenericPathRouter` + dispatch engine `PATH` strategy (path-only, order-based matching).
- `AbstractActor` strategies (class, key, enum, command, etc.).

Upcoming HTTP-like entry points require evaluating both a transport action (e.g., HTTP method) and a path with flexible allow/deny logic. Rather than embedding this logic in adapters, we should create an engine-backed strategy to keep routing consistent and extensible.

## Solution Overview
1. **Dispatch Strategy Expansion**
   - Add `ACTION_PATH` to `DispatchStrategyType`.
   - Introduce `ActionPathDispatchRequest<TResult, TAction>` carrying runtime/request context, resolved path match, and the incoming action token.
   - Add `ActionPathDispatchConfig<TResult, TAction>` describing registered handlers plus metadata describing mode and action sets per route.
   - Implement `ActionPathDispatchHandler<TResult, TAction>` entries that evaluate the configured mode:
     - `ALL`: return handler whenever path matched.
     - `WHITELIST`: succeed only if action is contained in `allowedActions`.
     - `BLACKLIST`: succeed if action not contained in `deniedActions`.

2. **GenericActionAndPathRouter**
   - Mirror `GenericPathRouter` registration but accept an `ActionMatchMode` (ALL/WHITELIST/BLACKLIST) and optional action set.
   - Maintain underlying `RouteRegistration` list for path detection, but convert each registration into an `ActionPathDispatchHandler` that wraps matcher + handler + mode metadata.
   - Expose helper methods such as `registerAllActions`, `registerWhitelist`, `registerBlacklist`, plus a low-level `register(RouteRegistration, ActionMatchMode, Set<TAction>)`.

3. **Generics**
   - Use `<TAction>` as a router type parameter so business code can supply strings, enums, or custom action types without casting.
   - Ensure dispatch engine config stores `Set<TAction>` references generically (no raw types).

4. **Testing**
   - Create dispatch-engine tests covering each match mode with `String` and enum actions to ensure generics behave.
   - Add router tests verifying that:
     - Path match without action still works via ALL mode.
     - WHITELIST rejects actions outside the set.
     - BLACKLIST rejects actions inside the set.
     - `null` actions are handled according to the mode (ALL accepts, others evaluate membership with null included/excluded).

## Considerations
- Preserve existing `GenericPathRouter` semantics; action-aware router should not break it.
- Avoid incidental coupling to HTTP concepts; keep naming generic (Action, Path, MatchMode).
- Provide clear exceptions when required sets are missing for whitelist/blacklist registrations.
