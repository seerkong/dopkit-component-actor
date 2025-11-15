# 更新日志

遵循 Keep a Changelog 风格，列出影响使用者的变更。版本号与 `pom.xml` 中的 `1.0.0-SNAPSHOT` 独立管理，可按需同步。

## [Unreleased]
- 文档重构：所有 Markdown 文件迁移至 `doc/`，新增架构、Actor、业务封装层指南。
- README 更新以匹配当前代码结构与 Actor 功能集。

## [0.3.0] - Component Actor + CommandTable
### Added
- `actor` 包：`ActorRoute`, `ActorRouteBuilder`, `AbstractActor`, `IActor`。
- 5 种分发机制：Class、RouteKey、Enum、RouteKey→Enum、CommandTable。
- 示例与测试：`actor/example/UserApiTest`、`actor/command/UserCommandApiTest`。

### Changed
- README 与示例展示 Actor 写法。

## [0.2.0] - 业务封装层
### Added
- `business` 目录：`ApiAdapter`、`FuncStyleApiAdapter`、`OOPStyleApiAdapter` 等类型。
- 示例适配器：`UserSearchAdapter`, `UserCreateAdapter`, `UserGetByNameAdapter`。
- 静态路由表 `UserApiAdapterRouter` 与测试 `UserApiAdapterRouterTest`。

### Changed
- `StdRunComponentLogic` 正式作为业务层的统一入口，`OOPStyleApiAdapter` 的 `dispatch` 与函数式保持一致。

## [0.1.0] - 核心框架
### Added
- `component` 包：标准组件封装协议及执行引擎。
- `router` 包：`GenericRouter`, `RouteRegistration`, `RouteMatcher`, `ComponentHandler`。
- 示例与测试：`example/UserApiRouterTest`, `example/AdvancedOOPStyleTest`。

### Notes
- 核心层保持零业务依赖，仅使用 JUnit/Guava/Lombok 作为测试或工具。
