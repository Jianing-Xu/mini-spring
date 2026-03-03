# Three-Level Cache Phase 1 验收用例清单

> **phase_n**: 1  
> **injection_style**: SETTER_REQUIRED  
> **circular_dependency**: THREE_LEVEL_CACHE  
> **aop_early_reference**: DISABLED  
> **scope_support**: SINGLETON_ONLY  
> **覆盖策略**：正常路径（setter 循环闭环）+ 失败路径（构造器 FAIL_FAST）+ 缓存状态验证

---

## 1. Setter 循环依赖解决 — 正常路径

### TC-1.1 直接循环（A ↔ B）

| 维度  | 描述                                                                           |
|-------|--------------------------------------------------------------------------------|
| Given | `SetterCircularA` 有 setter / @Autowired 字段 `SetterCircularB b`              |
| Given | `SetterCircularB` 有 setter / @Autowired 字段 `SetterCircularA a`              |
| Given | 两者均 `@Component` + singleton                                                 |
| When  | 容器启动                                                                        |
| Then  | 启动成功，不抛出异常                                                            |
| Then  | `getBean(SetterCircularA.class).getB() == getBean(SetterCircularB.class)`       |
| Then  | `getBean(SetterCircularB.class).getA() == getBean(SetterCircularA.class)`       |

### TC-1.2 三层循环（A → B → C → A）

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `CycleA` → `CycleB` → `CycleC` → `CycleA`（setter / @Autowired 注入）         |
| When  | 容器启动                                                                       |
| Then  | 启动成功                                                                       |
| Then  | `CycleA.b == getBean(CycleB.class)`                                           |
| Then  | `CycleB.c == getBean(CycleC.class)`                                           |
| Then  | `CycleC.a == getBean(CycleA.class)`                                           |

### TC-1.3 四层循环（A → B → C → D → A）

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `D4A` → `D4B` → `D4C` → `D4D` → `D4A`（setter 注入）                         |
| When  | 容器启动                                                                       |
| Then  | 启动成功，所有引用一致                                                          |

### TC-1.4 自依赖

| 维度  | 描述                                                             |
|-------|------------------------------------------------------------------|
| Given | `SelfRefBean` 有 `@Autowired SelfRefBean self`                    |
| When  | 容器启动                                                          |
| Then  | 启动成功                                                          |
| Then  | `getBean(SelfRefBean.class).getSelf() == getBean(SelfRefBean.class)` |

### TC-1.5 菱形依赖（A → B, A → C, B → D, C → D）

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | A 依赖 B 和 C，B 和 C 各自依赖 D，无循环                                  |
| When  | 容器启动                                                                 |
| Then  | 启动成功，D 仅实例化一次（singleton）                                     |
| Then  | `A.b.d == A.c.d == getBean(D.class)`                                    |

### TC-1.6 非循环依赖 Bean 不受影响

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | `ServiceA` → `ServiceB`，`ServiceB` 无依赖（非循环）                      |
| When  | 容器启动                                                                 |
| Then  | 正常创建，`ServiceA.b == getBean(ServiceB.class)`                        |

---

## 2. 构造器循环依赖 — FAIL_FAST

### TC-2.1 直接构造器循环（A ↔ B）

| 维度  | 描述                                                                     |
|-------|--------------------------------------------------------------------------|
| Given | `CtorA` 构造器参数依赖 `CtorB`；`CtorB` 构造器参数依赖 `CtorA`            |
| When  | 容器启动                                                                  |
| Then  | 抛出 `BeanCurrentlyInCreationException`                                   |
| Then  | 异常 message 包含 `"ctorA"` 和 `"ctorB"` 的依赖关系                       |

### TC-2.2 混合注入循环（构造器 + setter）

| 维度  | 描述                                                                       |
|-------|----------------------------------------------------------------------------|
| Given | `MixA` 构造器依赖 `MixB`；`MixB` setter 依赖 `MixA`                        |
| When  | 容器启动                                                                    |
| Then  | 抛出 `BeanCurrentlyInCreationException`（构造器端在实例化阶段失败）           |

---

## 3. Singleton 一致性

### TC-3.1 循环依赖 Bean 多次 getBean 一致

| 维度  | 描述                                                                 |
|-------|----------------------------------------------------------------------|
| Given | A ↔ B 循环依赖已解决                                                 |
| When  | 调用 `getBean(SetterCircularA.class)` 5 次                            |
| Then  | 5 次返回同一实例 (`assertSame`)                                       |

### TC-3.2 循环引用方持有的引用与容器一致

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | A ↔ B 循环依赖已解决                                                    |
| When  | 取出 A 和 B 实例                                                         |
| Then  | `A.b` 与 `getBean(B.class)` 是同一对象                                  |
| Then  | `B.a` 与 `getBean(A.class)` 是同一对象                                  |

---

## 4. 缓存状态验证

### TC-4.1 启动后二级缓存为空

| 维度  | 描述                                                   |
|-------|--------------------------------------------------------|
| Given | 容器包含循环依赖和非循环依赖的 Bean                      |
| When  | 容器启动完成                                            |
| Then  | `earlySingletonObjects.size() == 0`                     |

### TC-4.2 启动后三级缓存为空

| 维度  | 描述                                                   |
|-------|--------------------------------------------------------|
| Given | 容器包含循环依赖和非循环依赖的 Bean                      |
| When  | 容器启动完成                                            |
| Then  | `singletonFactories.size() == 0`                        |

### TC-4.3 启动后创建中标记为空

| 维度  | 描述                                                   |
|-------|--------------------------------------------------------|
| Given | 容器包含循环依赖和非循环依赖的 Bean                      |
| When  | 容器启动完成                                            |
| Then  | `singletonsCurrentlyInCreation.size() == 0`              |

### TC-4.4 所有 singleton Bean 仅存在于一级缓存

| 维度  | 描述                                                        |
|-------|-------------------------------------------------------------|
| Given | 注册了 N 个 singleton Bean（含循环依赖场景）                  |
| When  | 容器启动完成                                                 |
| Then  | `singletonObjects.size() >= N`                               |
| Then  | 每个 Bean 的 beanName 仅在 `singletonObjects` 中存在          |

---

## 5. 异常后缓存清理

### TC-5.1 populateBean 异常后缓存清理

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `FailingBean` 的 @Autowired 字段类型不存在（注入失败）                          |
| When  | 容器启动                                                                       |
| Then  | 抛出 `BeansException`                                                          |
| Then  | `singletonFactories` 中不包含 `"failingBean"` 的条目                            |
| Then  | `singletonsCurrentlyInCreation` 中不包含 `"failingBean"`                        |

### TC-5.2 initializeBean 异常后缓存清理

| 维度  | 描述                                                                               |
|-------|------------------------------------------------------------------------------------|
| Given | `ErrorInitBean` 实现 `InitializingBean`，`afterPropertiesSet()` 抛出异常            |
| When  | 容器启动                                                                            |
| Then  | 抛出 `BeansException`                                                               |
| Then  | `singletonFactories` 和 `earlySingletonObjects` 中不包含 `"errorInitBean"` 的条目    |
| Then  | `singletonsCurrentlyInCreation` 中不包含 `"errorInitBean"`                           |

---

## 6. 工厂方法 Bean 参与循环依赖

### TC-6.1 @Bean 创建的 Bean 与 @Component 循环依赖

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `@Bean DataService dataService()` 返回的 `DataService` 有 `@Autowired UserService` |
| Given | `UserService`（@Component）有 `@Autowired DataService`                              |
| When  | 容器启动                                                                           |
| Then  | 启动成功，两者引用一致                                                              |

---

## 7. 边界场景

### TC-7.1 无任何循环依赖 — 三级缓存透明

| 维度  | 描述                                                    |
|-------|--------------------------------------------------------|
| Given | 注册 10 个无循环依赖的 singleton Bean                    |
| When  | 容器启动                                                |
| Then  | 全部正常创建，行为与无三级缓存时一致                      |
| Then  | 二三级缓存均为空                                         |

### TC-7.2 循环依赖中一方无 @Autowired 字段

| 维度  | 描述                                                                   |
|-------|------------------------------------------------------------------------|
| Given | `ServiceA` 有 `@Autowired ServiceB b`；`ServiceB` 无任何依赖            |
| When  | 容器启动                                                                |
| Then  | 非循环，正常启动                                                        |
