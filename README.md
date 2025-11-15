# DOPKit · Component Actor Framework

面向 Data-Oriented Programming 的通用组件封装与消息分发框架。核心关注两件事：
1. **Standard Component Pipeline** — `component` 包将一次组件调用拆分为可复用的运行时/输入/配置/输出转换步骤；
2. **Flexible Dispatch** — `router` 与 `actor` 包提供从 HTTP/RPC/MQ 等多种入口消息路由到 class 级别 Actor/CommandTable 的多种调度方式。

> Java 8+, Maven 项目，零业务依赖。测试示例使用 Guava、Lombok、JUnit 5。

## 核心特性

- **类型安全的分发机制**：支持按 Class、RouteKey、Enum、字符串→Enum、CommandTable 等 5 种方式路由到同一 handler。
- **标准化封装协议**：`StdRunComponentLogic` 保证所有组件都遵循“外层参数 → 内层输入 → 核心逻辑 → 外层输出”的固定顺序。
- **统一的路由抽象**：`GenericRouter` + `RouteRegistration` 支持函数式与 OOP 两种写法，可用于 HTTP、RPC、MQ 等场景。
- **可选业务封装层**：示例中的 `ApiAdapter`、`FuncStyleApiAdapter`、`OOPStyleApiAdapter` 将 9 个泛型降到 2 个，方便业务落地。
- **完善示例**：测试目录覆盖核心层、业务封装层、Actor/CommandTable，便于对照学习。

## 快速预览

```java
public class UserApi extends ApiEndpointBase {
    enum UserApiKey { SEARCH, GET_BY_USERNAME, CREATE }

    @Override
    protected ActorRoute<Result<?>> createActorRoute() {
        return ActorRouteBuilder.<Result<?>>create()
            .match(SearchUserRequest.class,
                   Sets.newHashSet("search", "searchByKeyword"),
                   Sets.newHashSet(UserApiKey.SEARCH),
                   this::search)
            .match(GetUserRequest.class,
                   Sets.newHashSet("user.get"),
                   Sets.newHashSet(UserApiKey.GET_BY_USERNAME),
                   this::getUserByUsername)
            .registerEnumConverter(UserApiKey.class, key -> tryConvert(key))
            .matchAny(input -> Result.fail("Unsupported input type: " + describe(input)))
            .build();
    }
}

UserApi api = new UserApi();
api.callTyped(new SearchUserRequest("alice"));              // 机制1：按 Class 分发
api.callByRouteKeyTyped("user.get", new GetUserRequest("bob")); // 机制2：RouteKey
api.callByEnumTyped(UserApiKey.SEARCH, req);                 // 机制3：Enum
api.callByRouteKeyTyped("search", req);                     // 机制4：字符串→枚举

UserCommandApi commandApi = new UserCommandApi();
commandApi.callByCommandTyped("SEARCH_USER", params);       // 机制5：CommandTable
```

更多示例：
- `actor/example/UserApiTest` —— 5 种调用方式与默认 handler 的断言。
- `actor/command/UserCommandApiTest` —— CommandTable，含命令别名、兜底处理。
- `business/UserApiAdapterRouterTest` —— 如何用 `GenericRouter` + 业务封装层驱动 HTTP API。

## 模块一览

| 包 | 说明 |
| --- | --- |
| `com.dopkit.component` | 标准组件封装协议（Outer/Inner adapter + `StdRunComponentLogic`）。
| `com.dopkit.router` | 泛型化路由器：`RouteMatcher`, `ComponentHandler`, `GenericRouter`。
| `com.dopkit.actor` | Actor 路由表、构建器、抽象基类以及 CommandTable 支持。
| `src/test/.../example` | 直接使用核心层的示例（无业务封装）。
| `src/test/.../business` | 业务封装层与静态路由表示例。
| `src/test/.../actor` | Actor 与 CommandTable 的完整演示。

## 开发与测试

```bash
# 安装依赖并运行全部测试
mvn test

# 只跑 Actor 示例
mvn test -Dtest=UserApiTest,UserCommandApiTest

# 只跑业务封装层
mvn test -Dtest=UserApiAdapterRouterTest
```

- Maven `pom.xml` 设置为 `maven.compiler.source/target=8`，可直接在 Java 8 及以上版本运行。
- 默认使用 `maven-surefire-plugin` 3.0.0。

## 文档

- [架构与模块说明](doc/architecture.md)
- [Actor 模式使用指南](doc/actor-mode.md)
- [更新日志](doc/changelog.md)

## 项目结构

```
.
├── README.md
├── doc/
├── pom.xml
├── src
│   ├── main/java/com/dopkit/{component,router,actor}
│   └── test/java/com/dopkit/{example,business,actor}
└── target/ (... build artifacts)
```

欢迎将核心模块嵌入实际项目：你可以选择直接使用 `GenericRouter`/`StdRunComponentLogic`，也可以照着示例搭建属于自己的业务封装层和 Actor 入口层。
