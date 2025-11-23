## Overview

We now have a reusable dispatch engine plus `GenericPathRouter`. The next step is to support entry points that must match both a transport action (HTTP method, RPC verb, etc.) and a path. We need an engine-backed router that understands action-aware routing with inclusive or exclusive rules so future HTTP-style adapters can plug in without custom code.

## Goals
- Introduce a new `GenericActionAndPathRouter` that routes by path while also evaluating an action token supplied by the caller.
- Extend the dispatch engine with an `ACTION_PATH` strategy (and request/config types) that understands three match modes:
  1. **ALL** — path match implies success regardless of action.
  2. **WHITELIST** — path match requires the action to exist within a provided allow-list.
  3. **BLACKLIST** — path match succeeds only if the action is *not* present in a provided deny-list.
- Keep the action token generic so callers can use strings, enums, or any other comparable type.
- Preserve existing behavior of path-only routing and actor strategies.

## Non-Goals
- Building HTTP-specific adapters or middleware on top of the new router (follow-up work).
- Changing how existing routes are registered outside of the action-aware router.
- Introducing additional match modes beyond the three defined above.

## Success Criteria
- Dispatch engine exposes the new strategy enum/config/request and executes it alongside existing strategies.
- `GenericActionAndPathRouter` can register mixed route entries (path-only, action-aware) and dispatch requests covering all three match modes.
- Tests cover success/failure cases for each match mode plus integration wiring with the dispatch engine.
