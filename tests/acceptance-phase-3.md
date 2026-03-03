# Phase 3 验收用例清单

> **phase_n**: 3  
> **config_source**: AnnotationScan  
> **circular_dependency**: THREE_LEVEL_CACHE_REQUIRED  
> **覆盖策略**：正常路径（循环依赖解决）+ 失败路径（不可解决的循环）+ AOP 一致性 + Phase 1/2 回归

---

## 1. 基础循环依赖解决

### TC-1.1 直接循环依赖（A ↔ B）— singleton 字段注入

| 维度  | 描述                                                                         |
|-------|------------------------------------------------------------------------------|
| Given | `CircularA` 有 `@Autowired CircularB b`；`CircularB` 有 `@Autowired CircularA a` |
| Given | 两者均 `@Scope("singleton")` + `@Component`                                  |
| When  | 容器启动                                                                      |
| Then  | 启动成功，不抛出异常                                                          |
| Then  | `getBean(CircularA.class).getB() == getBean(CircularB.class)`                 |
| Then  | `getBean(CircularB.class).getA() == getBean(CircularA.class)`                 |

### TC-1.2 三层循环依赖（A → B → C → A）

| 维度  | 描述                                                                               |
|-------|------------------------------------------------------------------------------------|
| Given | `CycleA` → `CycleB` → `CycleC` → `CycleA`（全部 singleton 字段注入）               |
| When  | 容器启动                                                                            |
| Then  | 启动成功                                                                            |
| Then  | `CycleA.b == getBean(CycleB.class)`                                                |
| Then  | `CycleB.c == getBean(CycleC.class)`                                                |
| Then  | `CycleC.a == getBean(CycleA.class)`                                                |

### TC-1.3 自依赖

| 维度  | 描述                                                              |
|-------|-------------------------------------------------------------------|
| Given | `SelfRefBean` 有 `@Autowired SelfRefBean self`                     |
| When  | 容器启动                                                           |
| Then  | 启动成功                                                           |
| Then  | `getBean(SelfRefBean.class).getSelf() == getBean(SelfRefBean.class)` |

### TC-1.4 非循环依赖不受影响

| 维度  | 描述                                                                   |
|-------|------------------------------------------------------------------------|
| Given | `ServiceA` → `ServiceB`、`ServiceB` 无依赖（非循环）                    |
| When  | 容器启动                                                                |
| Then  | 启动成功，`ServiceA.b == getBean(ServiceB.class)`                       |
| Then  | `ServiceB` 的 `singletonFactories` 条目在最终状态下已清除               |

---

## 2. AOP + 循环依赖一致性

### TC-2.1 单侧 AOP 代理 + 循环依赖

| 维度  | 描述                                                                                      |
|-------|-------------------------------------------------------------------------------------------|
| Given | `ProxiedA` ↔ `ProxiedB`，切点仅匹配 `ProxiedB`（`ProxiedB` 实现接口）                      |
| When  | 容器启动                                                                                   |
| Then  | `getBean(ProxiedB 接口.class) instanceof Proxy == true`                                    |
| Then  | `getBean(ProxiedA.class).getB() instanceof Proxy == true` — A 持有的 B 是代理               |
| Then  | `getBean(ProxiedA.class).getB() == getBean(ProxiedB 接口.class)` — 同一代理实例              |

### TC-2.2 双侧 AOP 代理 + 循环依赖

| 维度  | 描述                                                                                      |
|-------|-------------------------------------------------------------------------------------------|
| Given | `DualProxyA` ↔ `DualProxyB`，切点匹配两者（两者均实现接口）                                 |
| When  | 容器启动                                                                                   |
| Then  | `getBean(InterfaceA.class) instanceof Proxy == true`                                       |
| Then  | `getBean(InterfaceB.class) instanceof Proxy == true`                                       |
| Then  | A 持有的 B 引用 == `getBean(InterfaceB.class)`                                             |
| Then  | B 持有的 A 引用 == `getBean(InterfaceA.class)`                                             |

### TC-2.3 AOP 代理方法调用 — 循环依赖场景

| 维度  | 描述                                                                                  |
|-------|---------------------------------------------------------------------------------------|
| Given | TC-2.1 场景，`LoggingInterceptor` 拦截 `ProxiedB` 的方法                               |
| When  | 通过 `ProxiedA.getB().doSomething()` 调用 B 的方法                                     |
| Then  | `LoggingInterceptor.invoke()` 被调用                                                   |
| Then  | 目标方法返回值正确                                                                     |

### TC-2.4 getEarlyBeanReference 不重复触发

| 维度  | 描述                                                                                        |
|-------|--------------------------------------------------------------------------------------------|
| Given | `ProxiedA` ↔ `ProxiedB`，`ProxiedB` 需 AOP 代理                                            |
| When  | 容器启动完成                                                                                 |
| Then  | `ProxiedB` 的 `getEarlyBeanReference` 仅被调用 1 次                                         |
| Then  | `postProcessAfterInitialization` 对 `ProxiedB` 跳过代理创建（因 `earlyProxyReferences` 包含） |

---

## 3. 不可解决的循环依赖 — 快速失败

### TC-3.1 构造器注入循环依赖

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `CtorA` 构造器参数依赖 `CtorB`；`CtorB` 构造器参数依赖 `CtorA`                     |
| When  | 容器启动                                                                           |
| Then  | 抛出 `BeanCurrentlyInCreationException`                                            |
| Then  | 异常 message 包含依赖链和构造器注入不可解决的提示                                    |

### TC-3.2 prototype 参与循环依赖

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `SingletonA` 有 `@Autowired PrototypeB b`；`PrototypeB` 有 `@Autowired SingletonA a` |
| Given | `PrototypeB` 标注 `@Scope("prototype")`                                            |
| When  | 容器启动                                                                            |
| Then  | 抛出 `BeanCurrentlyInCreationException`                                             |

### TC-3.3 混合注入循环（构造器 + 字段）

| 维度  | 描述                                                                         |
|-------|------------------------------------------------------------------------------|
| Given | `MixA` 通过构造器依赖 `MixB`；`MixB` 通过 `@Autowired` 字段依赖 `MixA`       |
| When  | 容器启动                                                                      |
| Then  | 抛出 `BeanCurrentlyInCreationException`（构造器端在实例化阶段即失败）          |

---

## 4. 三级缓存内部状态验证

### TC-4.1 循环依赖后缓存清理

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `CircularA` ↔ `CircularB` 循环依赖已解决                                      |
| When  | 容器启动完成                                                                   |
| Then  | `singletonObjects` 包含 "circularA" 和 "circularB"                             |
| Then  | `earlySingletonObjects` 为空                                                   |
| Then  | `singletonFactories` 为空                                                      |

### TC-4.2 无循环依赖 Bean 的缓存状态

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `SimpleBean` 无任何依赖                                                        |
| When  | 容器启动完成                                                                   |
| Then  | `SimpleBean` 仅存在于 `singletonObjects`                                       |
| Then  | `SimpleBean` 的 `singletonFactories` 条目未被消费（直接清除）                   |

### TC-4.3 缓存升级验证（三级 → 二级）

| 维度  | 描述                                                                                      |
|-------|-------------------------------------------------------------------------------------------|
| Given | `CircularA` ↔ `CircularB`，在 B 的 populateBean 阶段触发 getSingleton("a")                 |
| When  | getSingleton("a") 命中三级缓存                                                             |
| Then  | 调用 factory.getObject() 后，"a" 从 singletonFactories 移除                                |
| Then  | "a" 被放入 earlySingletonObjects                                                           |
| Then  | 后续 getSingleton("a") 直接命中二级缓存，不再调用 factory                                   |

---

## 5. 边界场景

### TC-5.1 大量 Bean 无循环依赖 — 性能无退化

| 维度  | 描述                                                                |
|-------|---------------------------------------------------------------------|
| Given | 注册 20 个无循环依赖的 singleton Bean                                |
| When  | 容器启动完成                                                         |
| Then  | 全部 Bean 正常实例化                                                 |
| Then  | `earlySingletonObjects` 和 `singletonFactories` 均为空               |

### TC-5.2 多次 getBean 循环依赖 Bean — singleton 一致性

| 维度  | 描述                                                                      |
|-------|---------------------------------------------------------------------------|
| Given | `CircularA` ↔ `CircularB` 循环依赖已解决                                  |
| When  | 调用 `getBean(CircularA.class)` 5 次                                       |
| Then  | 5 次返回同一实例（`assertSame`）                                           |

---

## 6. Phase 1 + Phase 2 回归

### TC-6.1 Phase 1 全部用例回归

| 维度  | 描述                                                         |
|-------|--------------------------------------------------------------|
| Given | Phase 1 全部测试 Bean 和配置不变                              |
| When  | 在 Phase 3 代码基线上执行 Phase 1 全部验收用例                |
| Then  | 全部通过                                                      |

### TC-6.2 Phase 2 全部用例回归

| 维度  | 描述                                                         |
|-------|--------------------------------------------------------------|
| Given | Phase 2 全部测试 Bean 和配置不变                              |
| When  | 在 Phase 3 代码基线上执行 Phase 2 全部验收用例                |
| Then  | 全部通过（特别注意 Phase 2 AOP 代理、生命周期回调顺序）       |

### TC-6.3 Phase 1 循环依赖测试语义变更

| 维度  | 描述                                                                                 |
|-------|-------------------------------------------------------------------------------------|
| Given | Phase 1 TC-5.1（A ↔ B 循环依赖）测试 Bean，两者均为 singleton 字段注入                |
| When  | 在 Phase 3 代码基线上执行                                                             |
| Then  | **不再抛出异常**（Phase 3 已解决此场景） → 测试预期需从 "抛异常" 改为 "启动成功"       |
| Then  | A.b == getBean(B) && B.a == getBean(A)                                               |
