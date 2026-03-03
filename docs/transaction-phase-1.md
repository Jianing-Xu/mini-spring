# Transaction Phase 1：MVP 本地事务支持

> **mode**: PHASE  
> **phase_n**: 1  
> **tx_model**: LOCAL_THREAD_BOUND  
> **proxy_mode**: JDK_PROXY_ONLY  
> **package group**: `com.xujn`

---

## 1. 目标与范围

### 必须实现

| # | 能力 | 完成标志 |
|---|------|----------|
| 1 | `@Transactional` 注解 | 标注方法或类后，容器可识别事务元数据 |
| 2 | 事务属性解析 | 方法优先、类级别回退 |
| 3 | `PlatformTransactionManager` | 提供 `getTransaction` / `commit` / `rollback` |
| 4 | `TransactionInterceptor` | 事务方法调用前后自动管理事务 |
| 5 | `TransactionSynchronizationManager` | 事务资源绑定到当前线程 |
| 6 | REQUIRED 语义 | 外层存在事务时直接参与 |
| 7 | 默认回滚规则 | `RuntimeException` / `Error` 回滚，受检异常默认提交 |
| 8 | 事务自动代理 | `@Transactional` Bean 可被自动代理 |
| 9 | 示例与验收 | 本地可运行，无外部数据库依赖 |

### 不做（Phase 1 边界）

| 排除项 | 延后 |
|--------|------|
| `REQUIRES_NEW` / `NESTED` | Transaction Phase 3 |
| `rollbackFor` / `noRollbackFor` | Transaction Phase 3 |
| JDBC 真正接入 | Transaction Phase 2 |
| CGLIB 事务代理 | 不在当前计划 |
| 无接口 Bean 的事务代理 | 不在当前计划 |

---

## 2. 设计落地

### 2.1 注解模型

```text
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional
```

MVP 不增加额外属性，默认等价于：

- propagation = REQUIRED
- rollback on `RuntimeException` / `Error`

### 2.2 关键类清单

| 类 | 职责 |
|----|------|
| `Transactional` | 事务声明注解 |
| `TransactionDefinition` | 事务定义常量与回滚规则 |
| `TransactionStatus` / `SimpleTransactionStatus` | 当前事务状态 |
| `PlatformTransactionManager` | 事务管理器顶层接口 |
| `TransactionResource` | 本地事务资源抽象 |
| `TransactionSynchronizationManager` | 线程资源绑定 |
| `TransactionAttribute` | 解析后的事务属性 |
| `TransactionAttributeSource` | 方法 / 类级事务属性读取 |
| `AnnotationTransactionAttributeSource` | 基于 `@Transactional` 的实现 |
| `TransactionInterceptor` | 事务 around 拦截器 |
| `TransactionAutoProxyCreator` | 为事务 Bean 创建代理 |

### 2.3 自动代理策略

事务自动代理器判断规则：

1. Bean 不是基础设施 Bean
2. 目标类实现至少一个接口
3. 目标类或接口方法上存在 `@Transactional`

满足后返回 JDK 动态代理，拦截器为 `TransactionInterceptor`。

### 2.4 事务资源模型

MVP 使用内存事务资源进行验收：

```text
interface TransactionResource
    void begin()
    void commit()
    void rollback()
    boolean isActive()
```

验收可通过一个 `RecordingTransactionResource` 记录：

- begin 次数
- commit 次数
- rollback 次数
- 当前是否 active

### 2.5 回滚规则

| 异常类型 | 行为 |
|----------|------|
| `RuntimeException` | rollback |
| `Error` | rollback |
| checked exception | commit |

---

## 3. 关键流程

### 3.1 事务方法成功执行

```text
invoke()
  -> resolve transaction attribute
  -> getTransaction()
  -> proceed()
  -> commit()
  -> cleanup thread-bound resource
```

### 3.2 事务方法抛出运行时异常

```text
invoke()
  -> getTransaction()
  -> proceed() throws RuntimeException
  -> rollback()
  -> cleanup thread-bound resource
  -> rethrow
```

### 3.3 已存在事务时参与事务

```text
outer @Transactional method
  -> begin(new transaction)
  -> call inner @Transactional method
     -> detect existing resource
     -> join existing transaction
  -> outer commit
```

---

## 4. 验收映射

| 场景 | 验收入口 |
|------|----------|
| 成功提交 | `tests/acceptance-transaction-phase-1.md / TC-1.1` |
| 运行时异常回滚 | `TC-1.2` |
| checked exception 默认提交 | `TC-1.3` |
| 方法级事务覆盖类级事务 | `TC-2.1` |
| 类级 `@Transactional` 生效 | `TC-2.2` |
| REQUIRED 参与已有事务 | `TC-3.1` |
| 同线程资源清理 | `TC-3.2` |
| 无接口 Bean 不代理 | `TC-4.1` |
| 自调用不经过事务代理 | `TC-4.2` |

---

## 5. 风险点

| 风险 | 处理 |
|------|------|
| 自调用失效 | 文档明确限制，验收单独覆盖 |
| 多个 AutoProxyCreator 可能冲突 | MVP 先单独引入事务代理创建器 |
| 事务与普通 AOP 顺序问题 | MVP 不要求和已有 AOP 组合排序 |
