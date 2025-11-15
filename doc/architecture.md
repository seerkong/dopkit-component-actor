# 架构与模块说明

本文档根据 `src/main/java` 与 `src/test/java` 中的最新代码整理，覆盖 DOPKit Component Actor Framework 当前的全部模块。

## 顶层分层

```
src/main/java/com/dopkit/
├── component  # 标准组件封装协议（参数/运行时/配置/结果的转换流程）
├── router     # 泛型化路由注册与分发
└── actor      # 基于 Actor 的 class 级别消息分发（含 CommandTable 模式）

src/test/java/com/dopkit/
├── example    # 直接使用核心层的示例（GenericRouter + StdRunComponentLogic）
├── business   # 业务封装层示例（ApiAdapter + Func/OOP 两种写法）
└── actor      # Actor 与 CommandTable 的完整示例与测试
```

每一层都构建在 `component` 的标准封装流程之上：无论是 HTTP API 适配器、路由器还是 Actor，都通过组合 `StdRunComponentLogic` 来确保参数转换的顺序与语义一致。

## 标准组件封装协议（component）

`StdRunComponentLogic.runByFuncStyleAdapter` 将一次组件调用拆分为 5 个线性阶段：

1. `StdOuterComputedAdapter`：从外层输入推导额外信息，例如 `pathVariables`。
2. `StdInnerRuntimeAdapter`：确定内层逻辑需要的运行时（可直接透传）。
3. `StdInnerInputAdapter`：构造业务真正需要的输入对象。
4. `StdInnerConfigAdapter`：组装配置或上下文（示例中通常透传）。
5. `StdCoreLogic`：执行核心逻辑并产出结果。
6. `StdOuterOutputAdapter`：将内层输出转换成外层希望的返回体。

`StdOOPStyleAdapter` 则将上述 5 个 Adapter 与核心逻辑组合在一个接口里，方便以 OOP 方式实现。

> 示例参考：`src/test/java/com/dopkit/example/UserApiRouterTest` 中所有路由注册都通过 `StdRunComponentLogic` 串联输入/输出转换。

## 通用路由分发器（router）

`GenericRouter<TRuntime, TRequest, TMatchResult, TResponse>` 只做一件事：顺序遍历 `RouteRegistration`，对请求执行匹配并调用相应 handler。它与业务数据结构无关，匹配器、handler、match result 的类型都由使用方定义。

两种常见写法：

- **函数式注册**：直接传入 `RouteMatcher` Lambda 与 `ComponentHandler` Lambda。见 `UserApiRouterTest#testFunctionalStyleRegistration`。
- **OOP 注册**：继承 `RouteRegistration`，在构造函数中准备 matcher/handler。示例：`AdvancedOOPStyleTest.GetUserAdapter`。

因为 `GenericRouter` 完全基于泛型，可以用于 HTTP、RPC、MQ 甚至 CLI 命令。测试中的 `PathMatcher` 只是演示如何在不依赖 Web 框架的情况下解析路径。

## Actor 模块

`AbstractActor<TResult>` 在 `GenericRouter` 之上提供 class 级别的消息分发与 5 种内置机制：

| 机制 | API | 行为 |
| --- | --- | --- |
| 1 | `call(input)` | 依据输入对象的 Class 查找 handler。
| 2 | `callByRouteKey(key, input)` | 以字符串 key 映射 handler。
| 3 | `callByEnum(enum, input)` | 以枚举值映射 handler。
| 4 | `callByRouteKey` + `registerEnumConverter` | 字符串 key 自动尝试转换为枚举后再走机制 3。
| 5 | `callByCommand(command, input)` | CommandTable：字符串 -> 枚举 -> handler，常用作系统入口层分发。

`ActorRouteBuilder` 负责：

- 将同一个 handler 同时注册到 Class、RouteKey、Enum。
- 注册默认 handler（`matchAny`, `matchAnyKey`, `matchAnyEnum`）。
- 注册字符串 -> Enum 的辅助转换器。
- 配置 CommandTable（converter + handlerExtractor + defaultHandler）。

示例：

- `src/test/java/com/dopkit/actor/example/UserApi` 展示机制 1-4。
- `src/test/java/com/dopkit/actor/command/UserCommandApi` 展示机制 5，通过 `UserCommandHandlerTable` 管理 handler。

## 业务封装层

`src/test/java/com/dopkit/business` 演示如何在核心层上封装一层“固定外层类型”的 API 适配器：

- `ApiAdapter` 统一暴露 `dispatch(ApiRuntime, ApiRequest, Map<String,String>)`。
- `FuncStyleApiAdapter` 通过静态字段组合输入适配器与核心逻辑。
- `OOPStyleApiAdapter` 只要求实现 `makeInnerInput` 与 `runCoreLogic`，其余工作交给 `StdRunComponentLogic`。
- `UserApiAdapterRouter` 维护一个静态 `List<ApiAdapter>`，强调路由顺序（通配符放最后）。

该层只是示例，真实项目可以仿造它为 RPC、MQ、GraphQL 等场景自定义封装。

## 测试概览

| 类 | 关注点 |
| --- | --- |
| `example/UserApiRouterTest` | 纯核心层（GenericRouter + StdRunComponentLogic）函数式/OOP注册混用。
| `example/AdvancedOOPStyleTest` | 更复杂的 `RouteRegistration`/`StdOOPStyleAdapter` 组合。
| `business/UserApiAdapterRouterTest` | 业务封装层的静态路由表 + Func/OOP 混合写法。
| `actor/example/UserApiTest` | Actor 机制 1-4、默认 handler、异常路径。
| `actor/command/UserCommandApiTest` | CommandTable 机制 5 的完整流程与容错。

执行 `mvn test` 会运行全部测试，覆盖 `component`、`router`、`actor` 三个模块以及业务封装层示例。
