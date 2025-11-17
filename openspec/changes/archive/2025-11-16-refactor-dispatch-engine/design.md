## Problem
Routing logic is duplicated in `AbstractActor` (class, key, enum, enum conversion, command table) and in `GenericRouter` (path matcher). Each strategy has bespoke data structures, making it difficult to add a new dispatch mechanism such as HTTP method + path. We also lack a single place to describe which strategy is active and what configs it needs.

## Proposed Architecture
1. **DispatchStrategyType enum** — enumerates every built-in strategy (`CLASS`, `ROUTE_KEY`, `ENUM`, `ROUTE_KEY_TO_ENUM`, `COMMAND_TABLE`, `PATH`). Acts as the primary key in configs and engine registrations.
2. **DispatchStrategyConfig** — a tagged configuration object where each strategy-specific config lives in its own optional field (e.g., `ClassDispatchConfig`, `RouteKeyDispatchConfig`, etc.). The config includes:
   - The strategy `type`.
   - Strategy-specific properties, such as handler maps, enum converters, command handler tables, or path matchers.
3. **DispatchEngine** — interface with `registerStrategy(DispatchStrategyConfig)` and `DispatchResult dispatch(DispatchRequest)`; implementations can look up the strategy by type and execute it using the typed config.
4. **DispatchStrategyExecutor** — functional interface per strategy to keep logic isolated. Executors accept typed inputs (e.g., class map + payload) and return standardized dispatch outcomes (handler function, or error).
5. **DispatchContext / DispatchRequest** — describes the incoming invocation (input object, optional key, enum literal, path, etc.) so the engine can evaluate only the relevant strategies.

## Integration Plan
### AbstractActor
- Build an engine during Actor route creation by registering class, key, enum, route-key-to-enum, and command-table strategy configs.
- Replace direct map lookups with a call to `DispatchEngine.dispatch(...)`, mapping Actor method arguments (input object, route key, enum, command) into dispatch requests.
- Preserve existing default handler hooks by supplying fallback executors or by configuring the engine with default responses.

### GenericRouter
- Wrap the existing path matcher + handler list into a `PATH` strategy config and invoke the engine to determine the handler for each request.
- Provide a minimal dispatch request (path + runtime/request objects) tailored to router use cases.

## Extensibility
- Adding HTTP method + path becomes a matter of introducing `DispatchStrategyType.HTTP_ROUTE` and a matching config/executor pair without touching Actor or router classes.
- The engine can hold multiple configs simultaneously, so both Actor and router scenarios can install their own subsets of strategies.

## Testing Approach
- Existing unit tests (`actor/example`, `actor/command`, `example`, `business`) remain the safety net.
- Add focused tests for the engine itself to verify strategy registration, execution ordering, and error reporting.
