## 1. Planning
- [x] 1.1 Finalize dispatch strategy inventory (existing Actor strategies + GenericRouter path) and document gaps.
- [x] 1.2 Author design for the reusable dispatch engine API surface and integration plan.

## 2. Implementation
- [x] 2.1 Implement the dispatch strategy enum, configuration types, and engine interface with pluggable executors.
- [x] 2.2 Refactor `AbstractActor` to register its five strategies through the engine without behavior changes.
- [x] 2.3 Refactor `GenericRouter` path matching to use the same engine.
- [x] 2.4 Add or update unit tests covering Actor routes, command table, and router path matching to ensure regressions are caught.

## 3. Validation
- [x] 3.1 Run `mvn test`.
- [x] 3.2 Validate specs via `openspec validate refactor-dispatch-engine --strict`.
