# Transaction Phase 1 验收用例清单

> **phase_n**: 1  
> **tx_model**: LOCAL_THREAD_BOUND  
> **proxy_mode**: JDK_PROXY_ONLY  
> **覆盖策略**：正常路径 + 回滚路径 + 线程资源清理 + 代理边界

---

## 1. 提交与回滚

### TC-1.1 成功执行提交事务

| 维度 | 描述 |
|------|------|
| Given | `OrderService.placeOrder()` 标注 `@Transactional` |
| Given | 底层 `TransactionResource` 记录 begin / commit / rollback 次数 |
| When | 调用代理对象的 `placeOrder()` 且方法正常返回 |
| Then | `begin == 1` |
| Then | `commit == 1` |
| Then | `rollback == 0` |

### TC-1.2 运行时异常触发回滚

| 维度 | 描述 |
|------|------|
| Given | `OrderService.failWithRuntimeException()` 标注 `@Transactional` |
| When | 方法抛出 `RuntimeException` |
| Then | `begin == 1` |
| Then | `commit == 0` |
| Then | `rollback == 1` |
| Then | 原异常继续抛出 |

### TC-1.3 checked exception 默认提交

| 维度 | 描述 |
|------|------|
| Given | `OrderService.failWithCheckedException()` 标注 `@Transactional` |
| When | 方法抛出 checked exception |
| Then | `commit == 1` |
| Then | `rollback == 0` |
| Then | 原异常继续抛出 |

---

## 2. 事务属性解析

### TC-2.1 方法级注解优先于类级

| 维度 | 描述 |
|------|------|
| Given | 类上标注 `@Transactional`，方法上也标注 `@Transactional` |
| When | 调用该方法 |
| Then | 优先读取方法级事务属性 |

### TC-2.2 类级 `@Transactional` 生效

| 维度 | 描述 |
|------|------|
| Given | 类上标注 `@Transactional`，方法上未标注 |
| When | 调用接口方法 |
| Then | 方法仍进入事务拦截流程 |

---

## 3. REQUIRED 与线程上下文

### TC-3.1 内层事务方法参与已有事务

| 维度 | 描述 |
|------|------|
| Given | `OuterService.outer()` 与 `InnerService.inner()` 都标注 `@Transactional` |
| Given | `outer()` 内部通过代理调用 `inner()` |
| When | 调用 `outer()` |
| Then | 整个调用链只开启一次新事务 |
| Then | 只在外层提交一次 |

### TC-3.2 调用结束后清理线程事务资源

| 维度 | 描述 |
|------|------|
| Given | 成功或异常结束一个事务方法调用 |
| When | 方法返回后检查 `TransactionSynchronizationManager` |
| Then | 当前线程不再持有事务资源 |

---

## 4. 代理边界

### TC-4.1 无接口 Bean 不创建事务代理

| 维度 | 描述 |
|------|------|
| Given | 某个 `@Transactional` Bean 未实现任何接口 |
| When | 容器启动 |
| Then | 不抛异常 |
| Then | 返回原始对象，不创建代理 |

### TC-4.2 自调用不经过事务代理

| 维度 | 描述 |
|------|------|
| Given | `UserService.outer()` 内部直接调用 `this.inner()` |
| Given | `inner()` 标注 `@Transactional` |
| When | 调用 `outer()` |
| Then | `inner()` 不会单独触发新的事务拦截 |

---

## 5. 示例入口要求

### TC-5.1 Happy Path Example

| 维度 | 描述 |
|------|------|
| Given | `examples/` 下提供事务成功与回滚示例 |
| When | 运行 main |
| Then | 输出 begin / commit / rollback 状态以及 PASS 标识 |
