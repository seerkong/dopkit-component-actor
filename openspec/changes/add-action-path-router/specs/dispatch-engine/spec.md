## ADDED Requirements

### Requirement: Action + Path Dispatch Strategy
The dispatch engine MUST provide an `ACTION_PATH` strategy that evaluates both a path match result and a caller-provided action token.

#### Scenario: Action match modes
- **GIVEN** a registered action+path handler specifying a match mode
- **WHEN** requests invoke the engine with matching path data
- **THEN**
  - In `ALL` mode the handler executes regardless of action (including `null`).
  - In `WHITELIST` mode the handler executes only if the action is inside the configured allow-set.
  - In `BLACKLIST` mode the handler executes only if the action is NOT inside the configured deny-set.

### Requirement: GenericActionAndPathRouter Uses the Engine
The framework MUST ship a `GenericActionAndPathRouter` that configures the `ACTION_PATH` strategy and exposes registration helpers for each match mode.

#### Scenario: Router dispatches by action and path
- **GIVEN** a router that registers handlers for ALL, WHITELIST, and BLACKLIST modes
- **WHEN** `dispatch(runtime, request, action)` is invoked
- **THEN** it uses the dispatch engine to resolve the correct handler based on both the path matcher and the action evaluation, returning `null` when no handler qualifies.
