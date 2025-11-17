# Project Context

## Purpose
DOPKit Component Actor Framework is a pure-Java library that standardizes how data-oriented components are wrapped and dispatched. It focuses on two goals: (1) providing a reusable multi-stage component pipeline (`StdRunComponentLogic`) so every HTTP/RPC/MQ facade shares the same runtime/input/config/output choreography, and (2) supplying a flexible routing layer (`GenericRouter`, `ActorRouteBuilder`, CommandTable) that can deliver type-safe handlers for class, route key, enum, or string-driven commands with zero business coupling.

## Tech Stack
- Java 8 (source/target) with heavy use of generics, builders, and functional interfaces to stay framework-agnostic.
- Apache Maven for builds (`maven-compiler-plugin` 3.11.0, `maven-surefire-plugin` 3.0.0) and dependency management.
- Guava 31.1-jre utilities inside the library/tests, Lombok 1.18.28 (provided scope) to reduce boilerplate in demos, and UTF-8 source encoding.
- JUnit Jupiter 5.9.3 for testing plus AssertJ-style fluent assertions embedded in the tests.
- Documentation, specs, and change control tracked under `openspec/` alongside Markdown guides in `doc/`.

## Project Conventions

### Code Style
- Stick to plain Java 8 with no external frameworks; keep packages under `com.dopkit.{component,router,actor}` and match test mirrors (`example`, `business`, `actor`).
- Prefer immutable state and stateless helpers; pass dependencies as parameters instead of singletons.
- Use descriptive `Std*Adapter`, `*Route`, `*Actor`, and `*Api` naming to highlight the role inside the pipeline.
- Guard public APIs with `Objects.requireNonNull` and explicit runtime type checks so handler mismatches fail fast.
- Keep comments bilingual (Chinese explanations plus concise English Javadoc) where they clarify intent.

### Architecture Patterns
- Layered core: `component` defines the six-phase pipeline (outer runtime/input/config → inner runtime/input/config → core logic → outer output), `router` builds generic routing tables around that pipeline, and `actor` adds higher-level dispatch (class, key, enum, CommandTable) powered by `ActorRoute`.
- Business wrappers (see `src/test/.../business`) demonstrate how to create adapters that reduce the generic surface area for API teams while still leaning on `StdRunComponentLogic`.
- All routing is explicit registration: `GenericRouter` iterates `RouteRegistration` entries, while `ActorRouteBuilder` consolidates matches and default handlers for diagnostics.
- CommandTable support lets entrypoints translate string commands into enums, fetch handlers from lookup tables, and fall back to uniform error builders to protect edges.

### Testing Strategy
- Source-of-truth scenarios live under `src/test/java` and cover each layer: component/router examples, business adapters, Actor examples, and CommandTable flows.
- Use JUnit 5 with descriptive test names to document behavior; Guava + Lombok-based fixtures keep tests concise.
- Run `mvn test` for the entire suite; narrow focus runs via `mvn test -Dtest=UserApiTest,UserCommandApiTest` (Actor) or `UserApiAdapterRouterTest` (business facade).
- Tests are expected to exercise default handlers, enum conversion, command fallbacks, and multi-stage adapter wiring so regressions show up quickly.

### Git Workflow
- Follow the OpenSpec-first flow: read `openspec/specs` and `openspec/project.md`, inspect `openspec list`, then scaffold a change under `openspec/changes/<change-id>/` with `proposal.md`, `tasks.md`, and spec deltas before coding.
- Use verb-led `change-id` names (e.g., `add-command-alias-support`) that map to short-lived feature branches; keep implementation work gated on approved proposals.
- Validate specs with `openspec validate <change-id> --strict`, keep `tasks.md` checklists truthful, and only merge when every task is checked off and tests pass.
- Default to PRs against the main branch with conventional, descriptive commit messages that reference the `change-id`.

## Domain Context
The framework targets teams practicing Data-Oriented Programming who need to normalize inputs/outputs and dispatch logic across heterogeneous channels (HTTP endpoints, RPC services, MQ consumers, CLI commands). Each component call goes through the same adapters so teams can plug in domain-specific runtimes/configs while keeping handler logic agnostic. Actor routes then let the same service expose type-safe entry points for internal code, enum-driven orchestration, or external command tables without duplicating routing logic.

## Important Constraints
- Must stay Java 8 compatible and avoid adding runtime dependencies beyond the current minimal set to keep the library embeddable.
- Core modules must remain free of business-specific code; only tests/demos may introduce sample adapters or enums.
- Handler registration order matters (more specific routes before catch-alls), so new routes should not rely on implicit priority.
- Default handlers and error builders should emit actionable diagnostics since the framework often sits at system boundaries.
- Specs and documentation (`doc/*.md`, `openspec/`) are authoritative; implementation must follow the documented pipeline stages and routing semantics.

## External Dependencies
- Guava (`com.google.guava:guava:31.1-jre`) for collections helpers used in routing/adapters.
- Lombok (`org.projectlombok:lombok:1.18.28`, provided) to trim boilerplate in examples without affecting consumers.
- JUnit Jupiter (`org.junit.jupiter:*:5.9.3`) for the full test matrix.
- Maven plugins (`maven-compiler-plugin 3.11.0`, `maven-surefire-plugin 3.0.0`) govern compilation/test execution and should remain aligned with Java 8 requirements.
