# Phase 2 验收用例清单

> **phase_n**: 2  
> **config_source**: AnnotationScan  
> **circular_dependency**: DISALLOW  
> **覆盖策略**：正常路径 + 失败路径 + 边界场景 + Phase 1 回归

---

## 1. InitializingBean 生命周期回调

### TC-1.1 afterPropertiesSet 正常调用

| 维度  | 描述                                                                     |
|-------|--------------------------------------------------------------------------|
| Given | `LifecycleBean` 实现 `InitializingBean`，内部设置 `initialized = true`    |
| When  | 容器启动完成                                                              |
| Then  | `getBean(LifecycleBean.class).isInitialized() == true`                    |

### TC-1.2 afterPropertiesSet 在 DI 之后调用

| 维度  | 描述                                                                                |
|-------|-------------------------------------------------------------------------------------|
| Given | `LifecycleBean` 实现 `InitializingBean`，`afterPropertiesSet()` 中读取 `@Autowired` 字段 |
| When  | 容器启动完成                                                                         |
| Then  | `afterPropertiesSet()` 中读取的依赖字段非 null                                        |

### TC-1.3 afterPropertiesSet 在 BPP Before 之后调用

| 维度  | 描述                                                                                 |
|-------|--------------------------------------------------------------------------------------|
| Given | 注册一个 BPP 记录 `beforeInit` 调用时间戳；`LifecycleBean` 记录 `afterPropertiesSet` 时间戳 |
| When  | 容器启动完成                                                                          |
| Then  | `beforeInit` 时间戳 < `afterPropertiesSet` 时间戳                                      |

### TC-1.4 未实现 InitializingBean 的 Bean — 无回调

| 维度  | 描述                                                         |
|-------|--------------------------------------------------------------|
| Given | `SimpleBean` 未实现 `InitializingBean`                        |
| When  | 容器启动完成                                                  |
| Then  | 正常获取 Bean，无异常抛出                                      |

---

## 2. DisposableBean 销毁回调

### TC-2.1 destroy 正常调用

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `DisposableService` 实现 `DisposableBean`，`destroy()` 中设置 `destroyed = true` |
| When  | 调用 `context.close()`                                                         |
| Then  | `destroyed == true`（通过在 close 前持有 Bean 引用验证）                         |

### TC-2.2 多个 DisposableBean 全部销毁

| 维度  | 描述                                                                 |
|-------|----------------------------------------------------------------------|
| Given | 3 个 singleton Bean 实现 `DisposableBean`，各自记录 `destroyed` 标志   |
| When  | 调用 `context.close()`                                                |
| Then  | 3 个 Bean 的 `destroyed` 标志全部为 `true`                             |

### TC-2.3 销毁异常隔离

| 维度  | 描述                                                                            |
|-------|---------------------------------------------------------------------------------|
| Given | `FailingDisposable.destroy()` 抛出 `RuntimeException`；`NormalDisposable.destroy()` 正常执行 |
| When  | 调用 `context.close()`                                                           |
| Then  | `NormalDisposable` 的 `destroy()` 仍然被调用                                      |
| Then  | `context.close()` 不抛出未捕获异常                                                |

### TC-2.4 Prototype Bean 销毁不调用

| 维度  | 描述                                                                       |
|-------|----------------------------------------------------------------------------|
| Given | `PrototypeDisposable` 标注 `@Scope("prototype")` 且实现 `DisposableBean`    |
| When  | 调用 `context.close()`                                                      |
| Then  | `PrototypeDisposable.destroy()` 不被容器调用                                 |

---

## 3. BeanPostProcessor

### TC-3.1 BPP Before 对每个 Bean 调用

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | 注册 `TrackingBPP`，在 `postProcessBeforeInitialization` 中记录 beanName         |
| Given | 容器中有 3 个业务 Bean                                                             |
| When  | 容器启动完成                                                                       |
| Then  | `TrackingBPP` 记录的 beanName 列表包含全部 3 个业务 Bean 的名称                     |

### TC-3.2 BPP After 对每个 Bean 调用

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | 注册 `TrackingBPP`，在 `postProcessAfterInitialization` 中记录 beanName           |
| Given | 容器中有 3 个业务 Bean                                                             |
| When  | 容器启动完成                                                                       |
| Then  | `TrackingBPP` 记录的 beanName 列表包含全部 3 个业务 Bean 的名称                     |

### TC-3.3 多个 BPP 按注册顺序执行

| 维度  | 描述                                                                            |
|-------|---------------------------------------------------------------------------------|
| Given | 注册 `BPP_A`（先）和 `BPP_B`（后），均记录调用顺序                                |
| When  | 容器启动完成                                                                     |
| Then  | 对每个 Bean，`BPP_A.before` 先于 `BPP_B.before` 调用                             |
| Then  | 对每个 Bean，`BPP_A.after` 先于 `BPP_B.after` 调用                               |

### TC-3.4 BPP 返回新对象替换原 Bean

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `ReplacingBPP.postProcessAfterInitialization` 对特定 Bean 返回一个包装对象     |
| When  | 容器启动完成                                                                   |
| Then  | `getBean(targetBeanName)` 返回的是包装对象，而非原始对象                        |

### TC-3.5 BPP 返回 null 时保留原对象

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | `NullBPP.postProcessBeforeInitialization` 返回 `null`                    |
| When  | 容器启动完成                                                             |
| Then  | Bean 实例保持不变（不为 null），后续流程正常执行                            |

### TC-3.6 BPP 在业务 Bean 之前实例化

| 维度  | 描述                                                                         |
|-------|------------------------------------------------------------------------------|
| Given | `EarlyBPP` 标注 `@Component` 且实现 `BeanPostProcessor`                      |
| Given | `BusinessBean` 标注 `@Component`                                              |
| When  | 容器启动完成                                                                  |
| Then  | `EarlyBPP` 的 `postProcessBeforeInitialization` 对 `BusinessBean` 生效        |

---

## 4. AOP 代理（JDK 动态代理）

### TC-4.1 切点匹配 — 代理创建

| 维度  | 描述                                                                                |
|-------|-------------------------------------------------------------------------------------|
| Given | `TestServiceImpl` 实现 `TestService` 接口，位于 `com.xujn.minispring.test.service`   |
| Given | 切点表达式 `execution(* com.xujn.minispring.test.service.*.*(..))`                    |
| When  | 容器启动完成                                                                         |
| Then  | `getBean(TestService.class) instanceof java.lang.reflect.Proxy == true`              |

### TC-4.2 切点不匹配 — 返回原对象

| 维度  | 描述                                                                                |
|-------|-------------------------------------------------------------------------------------|
| Given | `OtherBean` 位于 `com.xujn.minispring.test.other` 包                                |
| Given | 切点表达式仅匹配 `com.xujn.minispring.test.service` 包                                |
| When  | 容器启动完成                                                                         |
| Then  | `getBean(OtherBean.class) instanceof java.lang.reflect.Proxy == false`               |

### TC-4.3 MethodInterceptor 被调用

| 维度  | 描述                                                                                  |
|-------|--------------------------------------------------------------------------------------|
| Given | `LoggingInterceptor` 实现 `MethodInterceptor`，在 `invoke` 中设置 `intercepted = true` |
| Given | 切点匹配 `TestServiceImpl` 的方法                                                      |
| When  | 调用 `getBean(TestService.class).doSomething()`                                        |
| Then  | `LoggingInterceptor.intercepted == true`                                               |

### TC-4.4 目标方法正常执行

| 维度  | 描述                                                                     |
|-------|--------------------------------------------------------------------------|
| Given | `TestServiceImpl.doSomething()` 返回 `"result"`                           |
| Given | `LoggingInterceptor` 内部调用 `invocation.proceed()` 并透传返回值          |
| When  | 调用 `getBean(TestService.class).doSomething()`                           |
| Then  | 返回值为 `"result"`                                                       |

### TC-4.5 MethodInterceptor 修改返回值

| 维度  | 描述                                                                             |
|-------|----------------------------------------------------------------------------------|
| Given | `ModifyingInterceptor.invoke()` 调用 `proceed()` 后将返回值拼接 `"_modified"`     |
| When  | 调用代理方法                                                                      |
| Then  | 返回值为 `"result_modified"`                                                      |

### TC-4.6 无接口 Bean — 不创建代理

| 维度  | 描述                                                                      |
|-------|---------------------------------------------------------------------------|
| Given | `NoInterfaceBean` 未实现任何接口，切点表达式匹配其包路径                     |
| When  | 容器启动完成                                                               |
| Then  | `getBean(NoInterfaceBean.class)` 返回原始对象                              |
| Then  | 不抛出异常，容器正常启动                                                    |

### TC-4.7 代理对象的 DI 一致性

| 维度  | 描述                                                                                       |
|-------|-------------------------------------------------------------------------------------------|
| Given | `ConsumerService` 有 `@Autowired TestService`；`TestServiceImpl` 被 AOP 代理               |
| When  | 容器启动完成                                                                                |
| Then  | `ConsumerService.testService instanceof Proxy == true`                                     |
| Then  | `ConsumerService.testService == getBean(TestService.class)` — DI 注入的是代理对象           |

> [注释] Phase 2 代理与 DI 顺序约束：此用例成立的前提是 `ConsumerService` 在 `TestServiceImpl` 之后创建（或 `TestServiceImpl` 先完成 BPP After 阶段）。Phase 2 无循环依赖场景下该前提自然满足。Phase 3 循环依赖场景需要三级缓存保证。

---

## 5. Prototype 作用域

### TC-5.1 每次 getBean 返回新实例

| 维度  | 描述                                                              |
|-------|-------------------------------------------------------------------|
| Given | `PrototypeBean` 标注 `@Component` + `@Scope("prototype")`         |
| When  | 调用 `getBean(PrototypeBean.class)` 两次                           |
| Then  | 两次返回的实例 `assertNotSame`                                     |

### TC-5.2 Prototype 不存在于 singletonObjects

| 维度  | 描述                                                             |
|-------|------------------------------------------------------------------|
| Given | `PrototypeBean` 标注 `@Scope("prototype")`                       |
| When  | 调用 `getBean(PrototypeBean.class)`                               |
| Then  | `containsSingleton("prototypeBean") == false`                     |

### TC-5.3 Prototype 的 @Autowired 正常注入

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `PrototypeBean` 有 `@Autowired SingletonDep dep`                               |
| When  | 调用 `getBean(PrototypeBean.class)`                                            |
| Then  | `prototypeBean.getDep() != null`                                               |
| Then  | `prototypeBean.getDep() == getBean(SingletonDep.class)` — 注入的 singleton 一致 |

---

## 6. 完整生命周期顺序验证

### TC-6.1 生命周期钩子执行顺序

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `OrderTrackingBean` 实现 `InitializingBean`，所有方法记录调用顺序到共享 `List<String>` |
| Given | 注册 `OrderTrackingBPP`，before/after 方法同样记录调用顺序                          |
| When  | 容器启动完成                                                                       |
| Then  | 调用顺序列表严格为：`["populateBean", "bppBefore", "afterPropertiesSet", "bppAfter"]` |

### TC-6.2 AOP 代理 + 生命周期 — 综合验证

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `AopLifecycleService` 实现接口 + `InitializingBean` + `DisposableBean`             |
| Given | 切点匹配该 Bean，注册 `LoggingInterceptor`                                         |
| When  | 容器启动 → 调用代理方法 → `context.close()`                                        |
| Then  | `afterPropertiesSet()` 被调用                                                       |
| Then  | 代理方法调用经过 `LoggingInterceptor`                                               |
| Then  | `destroy()` 被调用                                                                  |

---

## 7. Phase 1 回归

### TC-7.1 注解扫描注册（Phase 1 回归）

| 维度  | 描述                                                                 |
|-------|----------------------------------------------------------------------|
| Given | Phase 1 全部测试 Bean 和配置不变                                      |
| When  | 在 Phase 2 代码基线上执行 Phase 1 全部验收用例                        |
| Then  | 全部通过                                                              |

### TC-7.2 循环依赖检测（Phase 1 回归）

| 维度  | 描述                                                                 |
|-------|----------------------------------------------------------------------|
| Given | `CircularA` ↔ `CircularB` 循环依赖（同 Phase 1 TC-5.1）              |
| When  | 在 Phase 2 代码基线上执行                                             |
| Then  | 抛出 `BeanCurrentlyInCreationException`，message 包含依赖链            |
