# dispatch-engine Specification

## Purpose
TBD - created by archiving change refactor-dispatch-engine. Update Purpose after archive.
## Requirements
### Requirement: Reusable Dispatch Strategy Engine
The framework MUST expose a reusable engine that owns all dispatch strategies currently offered by `AbstractActor` and `GenericRouter`.

#### Scenario: Engine provides enum-driven strategies
- **GIVEN** the framework boots
- **WHEN** code registers strategies via a central engine API using a `DispatchStrategyType` enum and matching config objects
- **THEN** every existing strategy (class, route key, enum, route key→enum, command table, path matcher) can be executed through the engine without bypassing it.

### Requirement: Strategy-Specific Configuration Objects
Each strategy MUST expose its own configuration object so callers can supply the data needed to run that strategy without affecting others.

#### Scenario: Route key to enum conversion uses dedicated config
- **GIVEN** a route key to enum strategy registration
- **WHEN** the strategy config includes a converter function and default handler fields
- **THEN** the engine uses those fields when dispatching and does not require unrelated strategies to provide the same data.

### Requirement: Existing Entry Points Consume the Engine
`AbstractActor` and `GenericRouter` MUST act as thin wrappers over the dispatch engine so that adding future strategies requires no further refactors.

#### Scenario: Actor and router delegate to engine
- **GIVEN** an actor route or router registration that previously used in-class maps
- **WHEN** the system runs
- **THEN** handler lookup flows through the dispatch engine, and the public behavior observed in actor/command/router tests remains unchanged.

