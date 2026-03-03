# mini-spring 事务（Transaction）架构设计文档

> **mode**: FULL  
> **tx_model**: LOCAL_THREAD_BOUND  
> **proxy_mode**: JDK_PROXY_ONLY  
> **config_source**: AnnotationScan + JavaConfig  
> **package group**: `com.xujn`

---

# 1. 背景与目标

## 1.1 为什么需要事务能力

| 维度 | 当前状态 | 引入事务后的价值 |
|------|----------|------------------|
| AOP | 已支持 JDK 动态代理与 `MethodInterceptor` | 可把事务作为横切关注点统一织入 |
| 生命周期 / IOC | 已支持 Bean 管理与依赖注入 | 可注入事务管理器与模板类 |
| 业务一致性 | 目前没有提交 / 回滚边界 | 方法级事务边界可保证本地资源一致性 |

**核心目标**：基于现有 AOP 最小闭环，补一版本地事务 MVP，使 `@Transactional` 方法可以自动开启、提交、回滚事务。

## 1.2 MVP 范围

### 必须实现

| # | 能力 | 定义 | 价值 | 最小闭环 |
|---|------|------|------|----------|
| 1 | `@Transactional` 注解 | 标注在方法或类上定义事务边界 | 声明式事务入口 | 注解元数据解析 |
| 2 | `PlatformTransactionManager` | 统一事务 begin / commit / rollback 抽象 | 隔离具体资源实现 | 本地事务管理器接口 |
| 3 | `TransactionStatus` | 表示当前事务状态 | 传递事务上下文 | `isNewTransaction` / `rollbackOnly` |
| 4 | `TransactionInterceptor` | AOP 拦截器，在方法调用前后控制事务 | 把事务织入业务方法 | begin → proceed → commit / rollback |
| 5 | `TransactionAttributeSource` | 从方法 / 类读取事务属性 | 统一解析逻辑 | 解析 `@Transactional` |
| 6 | `TransactionSynchronizationManager` | 用 `ThreadLocal` 绑定当前线程资源与事务状态 | 支持同线程资源复用 | 资源绑定 / 清理 |
| 7 | `DataSourceTransactionManager` MVP | 面向单资源、单线程、本地事务的事务管理器 | 完成可运行闭环 | 依赖 `TransactionResource` |

### 可选实现

| # | 能力 | 说明 |
|---|------|------|
| 8 | `rollbackFor` | 指定回滚异常类型 |
| 9 | `readOnly` | 只读标记 |
| 10 | `timeout` | 超时属性 |

### 不做

| 能力 | 原因 |
|------|------|
| 事务传播行为（`REQUIRES_NEW` / `NESTED` 等） | MVP 只支持 `REQUIRED` 语义 |
| 多数据源 / XA / JTA | 超出本地事务范围 |
| CGLIB 事务代理 | 当前 AOP 仅支持 JDK 代理 |
| 事务事件 / 同步回调列表 | 非 MVP 必需 |
| 响应式事务 | 非当前技术栈 |

## 1.3 术语表

| 术语 | 定义 |
|------|------|
| TransactionAttribute | 事务属性，描述是否启用事务、回滚策略等 |
| TransactionStatus | 当前事务状态对象 |
| PlatformTransactionManager | 事务管理器顶层接口 |
| TransactionResource | 被事务管理的底层资源抽象 |
| Thread-bound resource | 通过 `ThreadLocal` 绑定到当前线程的资源 |

---

# 2. 总体设计

## 2.1 包结构

```text
com.xujn.minispring
├── tx
│   ├── annotation
│   │   └── Transactional.java
│   ├── interceptor
│   │   ├── TransactionAttribute.java
│   │   ├── TransactionAttributeSource.java
│   │   ├── AnnotationTransactionAttributeSource.java
│   │   └── TransactionInterceptor.java
│   ├── support
│   │   ├── TransactionSynchronizationManager.java
│   │   └── TransactionAutoProxyCreator.java
│   └── transaction
│       ├── PlatformTransactionManager.java
│       ├── TransactionDefinition.java
│       ├── TransactionStatus.java
│       ├── SimpleTransactionStatus.java
│       ├── TransactionResource.java
│       └── DataSourceTransactionManager.java
└── exception
    └── TransactionException.java
```

## 2.2 核心交互

```text
@Transactional method
  -> TransactionAutoProxyCreator 判断是否需要代理
  -> TransactionInterceptor.invoke()
     -> TransactionAttributeSource 解析事务属性
     -> PlatformTransactionManager.getTransaction()
     -> invocation.proceed()
     -> commit() 或 rollback()
```

## 2.3 与现有 AOP 的关系

| 现有能力 | 事务设计中的使用方式 |
|----------|----------------------|
| `MethodInterceptor` | `TransactionInterceptor` 直接实现 |
| `AutoProxyCreator` | 复用模式，新增事务专用自动代理创建器 |
| JDK 动态代理 | 事务 Bean 仍要求目标实现接口 |

> [注释] 为什么事务优先基于现有 AOP 扩展
> - 背景：容器已经有 AOP 最小闭环，事务本质上也是一类 around advice
> - 影响：如果另起一套事务代理链，会重复引入代理工厂、匹配逻辑、代理一致性问题
> - 取舍：事务以 `MethodInterceptor` 形式复用现有 AOP 设施，仅新增事务属性解析和事务管理器抽象
> - 可选增强：后续可把事务自动代理并入统一 `AutoProxyCreator`

---

# 3. MVP 事务模型

## 3.1 TransactionDefinition

MVP 只保留：

- `PROPAGATION_REQUIRED`
- `rollbackOn(Throwable ex)` 默认：
  - `RuntimeException` / `Error` 回滚
  - 受检异常默认提交

## 3.2 TransactionStatus

最小字段：

| 字段 | 说明 |
|------|------|
| `newTransaction` | 是否由当前拦截器新开启 |
| `rollbackOnly` | 是否标记仅回滚 |
| `resource` | 当前事务持有的资源 |

## 3.3 TransactionResource

MVP 不直接绑定 JDBC `Connection`，而是抽象一个最小事务资源接口：

```text
interface TransactionResource
    void begin()
    void commit()
    void rollback()
    boolean isActive()
```

用途：

- 便于先完成框架事务闭环
- 后续可替换成真正的 JDBC `Connection` / `DataSource`

## 3.4 TransactionSynchronizationManager

线程上下文最小职责：

- 绑定当前线程事务资源
- 判断当前线程是否已存在事务
- 事务完成后清理上下文

```text
ThreadLocal<Map<Object, Object>> resources
```

MVP 约束：

- 仅单线程调用链
- 不支持跨线程传递

---

# 4. 核心流程

## 4.1 `TransactionInterceptor.invoke()`

```text
1. 解析 method / targetClass 对应的 TransactionAttribute
2. 若无事务属性 -> 直接 invocation.proceed()
3. 调用 txManager.getTransaction(attribute)
4. 执行业务方法
5. 成功 -> commit(status)
6. 异常 -> 根据 rollbackOn(ex) 决定 rollback 或 commit
7. finally 清理线程上下文
```

## 4.2 `PlatformTransactionManager`

```text
interface PlatformTransactionManager
    TransactionStatus getTransaction(TransactionDefinition definition)
    void commit(TransactionStatus status)
    void rollback(TransactionStatus status)
```

## 4.3 `DataSourceTransactionManager` MVP

行为约束：

- 若线程中不存在资源，则向 `TransactionResourceFactory` 获取新资源并 `begin()`
- 若线程中已存在资源，则直接复用，视为参与已有事务
- `commit()` / `rollback()` 只处理 `newTransaction=true` 的状态

> [注释] 为什么 MVP 先抽象资源而不直接接 JDBC
> - 背景：当前项目没有 JDBC / DataSource 主线设计文档，直接引入真实数据库会把事务 MVP 和持久层绑死
> - 影响：验收和示例将依赖外部数据库环境，破坏当前项目“可本地直接运行”的交付约束
> - 取舍：先使用 `TransactionResource` 抽象，把事务边界、线程绑定、提交回滚语义跑通；后续再增加 JDBC 适配层
> - 可选增强：新增 `JdbcTransactionResource` 与 `JdbcTransactionManager`

---

# 5. Phase 规划

## 5.1 Transaction Phase 1：MVP 本地事务

目标：

- `@Transactional` 方法可声明事务
- 支持本地 begin / commit / rollback
- 支持 `REQUIRED` 语义
- 支持同线程事务资源复用

包含：

- `@Transactional`
- `PlatformTransactionManager`
- `TransactionInterceptor`
- `TransactionSynchronizationManager`
- `TransactionResource` 抽象
- 自动代理创建器

不包含：

- 传播级别扩展
- JDBC 真正接入
- CGLIB 代理
- 嵌套事务

## 5.2 Transaction Phase 2：JDBC 事务接入

目标：

- 基于真实 `DataSource` / `Connection` 管理事务

## 5.3 Transaction Phase 3：传播行为与回滚规则增强

目标：

- `REQUIRES_NEW` / `NESTED`
- `rollbackFor`
- 更完整的事务属性模型

---

# 6. 风险与边界

| 风险 | 影响 | 处理策略 |
|------|------|----------|
| 仅 JDK 代理 | 无接口 Bean 无法事务增强 | Phase 1 明确 fail-open，不报错但不代理 |
| 自调用失效 | 同类内部方法调用绕过代理 | 文档明确限制 |
| 没有真实 JDBC | 只能验证框架事务语义 | Phase 1 用内存资源验收 |
| 多拦截器顺序 | 事务与其他 AOP 顺序可能影响行为 | Phase 1 固定事务代理创建器单独使用 |
