## 1. Planning
- [x] 1.1 Confirm action-aware routing requirements and document match modes + data structures.
- [x] 1.2 Design the dispatch engine extensions (strategy enum, configs, requests) and router API surface.

## 2. Implementation
- [x] 2.1 Add `ACTION_PATH` strategy types to the dispatch engine along with request/config objects and executor logic (including ALL/WHITELIST/BLACKLIST support).
- [x] 2.2 Implement `GenericActionAndPathRouter`, including registration helpers for each mode and integration with the dispatch engine.
- [x] 2.3 Add tests covering dispatch engine behavior for the new strategy and router-level scenarios across the three modes plus generic action typing.

## 3. Validation
- [x] 3.1 Run `mvn test`.
- [x] 3.2 Validate specs via `openspec validate add-action-path-router --strict`.
