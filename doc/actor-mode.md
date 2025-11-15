# Actor 模式使用指南

Actor 模块提供 `AbstractActor<TResult>` 及其配套的 `ActorRouteBuilder`，用于在同一个组件内按 **类型 / 字符串 RouteKey / 枚举 / Command** 进行消息分发。本指南结合 `src/test/java/com/dopkit/actor` 下的两个示例，总结 5 种分发机制及最佳实践。

## 快速上手：继承 `ApiEndpointBase`

`ApiEndpointBase`（测试目录中提供的业务基类）演示了最简单的继承方式：

```java
public class UserApi extends ApiEndpointBase {
    @Override
    protected ActorRoute<Result<?>> createActorRoute() {
        return ActorRouteBuilder.<Result<?>>create()
            .match(SearchUserRequest.class,
                   Sets.newHashSet("search", "searchByKeyword"),
                   Sets.newHashSet(UserApiKey.SEARCH),
                   this::search)
            // 其他 handler 省略
            .registerEnumConverter(UserApiKey.class, key -> tryConvert(key))
            .matchAny(input -> Result.fail("Unsupported input type: " + describe(input)))
            .matchAnyKey((key, input) -> Result.fail("Unsupported routeKey: " + key))
            .build();
    }

    private Result<List<User>> search(SearchUserRequest request) { ... }
}
```

建议：

1. **同一个 handler 同时注册多种入口**，用 `match` 一次性绑定 Class、RouteKey、Enum。
2. **默认处理器**（`matchAny`, `matchAnyKey`, `matchAnyEnum`）要给出可诊断信息。
3. **`createErrorResult`** 是兜底 fallback，可在基类中统一返回自定义 Result。

## 五种分发机制

| # | 方法 | 场景 | UserApi 示例 |
|---|------|------|---------------|
|1|`call(input)`| 代码内部调用、按 Class 精准映射 | `userApi.callTyped(new SearchUserRequest(...))`
|2|`callByRouteKey(key, input)`| 兼容字符串入口（HTTP path、RPC method 等）| `userApi.callByRouteKeyTyped("search", req)`
|3|`callByEnum(enum, input)`| 聚合根或 service 内部强类型调用 | `userApi.callByEnumTyped(UserApiKey.SEARCH, req)`
|4|`callByRouteKey` + `registerEnumConverter`| 字符串入口自动尝试映射到枚举，提高复用度 | `userApi.callByRouteKeyTyped("SEARCH", req)`
|5|`callByCommand(command, input)`| CommandTable：入口层通过字符串命令驱动，先映射枚举再定位 handler | `UserCommandApi.callByCommandTyped("SEARCH_USER", params)`

### CommandTable 详解（机制 5）

`UserCommandApi` 仅配置 CommandTable：

```java
protected ActorRoute<Result<?>> createActorRoute() {
    return ActorRouteBuilder.<Result<?>>create()
        .registerCommandTable(
            cmd -> tryConvert(cmd),                    // String -> Enum
            UserCommandHandlerTable::getHandlerFunction, // Enum -> handler
            (cmd, input) -> Result.fail("Command not supported: " + cmd)
        )
        .build();
}
```

- `commandConverter`：可自定义大小写或别名规则，失败返回 `null`。
- `handlerExtractor`：通常由一个 `Enum` + `handler` 表承载（示例中为 `UserCommandHandlerTable`）。
- `defaultHandler`：必须提供，负责兜底日志/监控/错误返回。

参考 `UserCommandApiTest` 可看到：

- 大小写不敏感 (`SEARCH_USER`, `search_user`, `Search_User`) 均可。
- 未实现的命令（`UPDATE_USER`）与不存在的命令（`UNKNOWN_COMMAND`）都回落到兜底 handler。
- handler 可自行校验参数、返回 `Result.ok(...)` 或 `Result.fail(...)`。

### handler 的编写建议

1. **保持幂等**：handler 可能被多个入口共享。
2. **参数校验在 handler 内部完成**，路由层只负责分发。
3. **不要在 handler 内缓存 `ActorRoute`**，`AbstractActor` 已做延迟初始化。
4. **测试**：
   - `UserApiTest` 覆盖所有机制及默认 handler；
   - `UserCommandApiTest` 覆盖 CommandTable 流程、参数校验与容错。

## 与其他模块的关系

- Actor 模块不依赖业务层，也不依赖 `router`，可单独用于任何 Java 项目。
- handler 内部仍可复用 `StdRunComponentLogic` 或直接调用 service/DAO。
- 可以将 Actor 包装成 HTTP/RPC endpoint，或把它作为聚合根内部的 command router。

## 调试提示

- `ActorRouteBuilder#match` 会对 handler 做类型检查，当输入类型不匹配时抛出详细异常。
- `callByRouteKey` 找不到 handler 时，会自动尝试 `registerEnumConverter` 注册过的所有转换器。
- `callByCommand` 在未配置 CommandTable 时会返回 `createErrorResult("CommandTable not configured")`，可据此快速定位配置遗漏。
