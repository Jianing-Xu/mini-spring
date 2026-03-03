# Phase 1 验收用例清单

> **phase_n**: 1  
> **config_source**: AnnotationScan  
> **circular_dependency**: DISALLOW  
> **覆盖策略**：正常路径 + 失败路径 + 边界场景

---

## 1. 注解扫描与 BeanDefinition 注册

### TC-1.1 单包扫描 — 正常注册

| 维度  | 描述                                                                 |
|-------|----------------------------------------------------------------------|
| Given | 包 `com.xujn.minispring.test.bean` 下存在 2 个 `@Component` 类和 1 个无注解类 |
| When  | 创建 `AnnotationConfigApplicationContext("com.xujn.minispring.test.bean")` |
| Then  | `getBeanDefinitionCount() == 2`                                       |
| Then  | `getBeanDefinitionNames()` 仅包含 2 个 `@Component` 类的 beanName     |

### TC-1.2 beanName 默认规则

| 维度  | 描述                                                         |
|-------|--------------------------------------------------------------|
| Given | `@Component` 标注类 `UserService`（无显式名称）                |
| When  | 扫描注册完成                                                  |
| Then  | `containsBeanDefinition("userService") == true`               |
| Then  | beanName 为类名首字母小写："userService"                       |

### TC-1.3 空包扫描

| 维度  | 描述                                                          |
|-------|---------------------------------------------------------------|
| Given | 指定包路径 `com.xujn.minispring.test.empty`，该包下无任何类     |
| When  | 扫描完成                                                       |
| Then  | `getBeanDefinitionCount() == 0`                                |
| Then  | 不抛出异常，容器正常启动                                        |

### TC-1.4 BeanDefinition 元数据正确性

| 维度  | 描述                                                                  |
|-------|-----------------------------------------------------------------------|
| Given | `@Component` + `@Scope("singleton")` 标注类 `OrderService`            |
| When  | 扫描注册完成                                                           |
| Then  | `getBeanDefinition("orderService").getBeanClass() == OrderService.class` |
| Then  | `getBeanDefinition("orderService").getScope().equals("singleton")`      |
| Then  | `getBeanDefinition("orderService").isSingleton() == true`               |

---

## 2. Bean 实例化与 getBean

### TC-2.1 按名称获取 Bean

| 维度  | 描述                                                    |
|-------|---------------------------------------------------------|
| Given | `@Component` 类 `UserService` 已注册                    |
| When  | 调用 `getBean("userService")`                            |
| Then  | 返回非 null 对象                                         |
| Then  | 返回对象 `instanceof UserService`                        |

### TC-2.2 按类型获取 Bean

| 维度  | 描述                                                    |
|-------|---------------------------------------------------------|
| Given | `@Component` 类 `UserService` 已注册                    |
| When  | 调用 `getBean(UserService.class)`                        |
| Then  | 返回非 null 对象                                         |
| Then  | 返回对象 `instanceof UserService`                        |

### TC-2.3 按名称 + 类型获取 Bean

| 维度  | 描述                                                          |
|-------|---------------------------------------------------------------|
| Given | `@Component` 类 `UserService` 已注册                          |
| When  | 调用 `getBean("userService", UserService.class)`               |
| Then  | 返回非 null 对象且 `instanceof UserService`                    |

### TC-2.4 Bean 不存在 — 按名称

| 维度  | 描述                                                          |
|-------|---------------------------------------------------------------|
| Given | 容器中未注册名为 `"nonExistent"` 的 Bean                       |
| When  | 调用 `getBean("nonExistent")`                                  |
| Then  | 抛出 `NoSuchBeanDefinitionException`                           |
| Then  | 异常 message 包含 `"nonExistent"`                               |

### TC-2.5 Bean 不存在 — 按类型

| 维度  | 描述                                                          |
|-------|---------------------------------------------------------------|
| Given | 容器中未注册类型 `PaymentService` 的 Bean                      |
| When  | 调用 `getBean(PaymentService.class)`                           |
| Then  | 抛出 `NoSuchBeanDefinitionException`                           |

---

## 3. Singleton 一致性

### TC-3.1 多次 getBean 返回同一实例

| 维度  | 描述                                                               |
|-------|--------------------------------------------------------------------|
| Given | `@Component` 类 `UserService`，scope 为默认 singleton               |
| When  | 调用 `getBean(UserService.class)` 两次，分别获得 `ref1` 和 `ref2`    |
| Then  | `ref1 == ref2`（`assertSame`）                                      |

### TC-3.2 containsBean 检查

| 维度  | 描述                                                    |
|-------|---------------------------------------------------------|
| Given | `@Component` 类 `UserService` 已注册并实例化             |
| When  | 调用 `containsBean("userService")`                       |
| Then  | 返回 `true`                                              |

---

## 4. 依赖注入（@Autowired 字段注入）

### TC-4.1 单层依赖注入

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | `UserService` 中有字段 `@Autowired UserRepository userRepository`       |
| Given | `UserRepository` 也标注 `@Component`                                     |
| When  | 容器启动完成                                                             |
| Then  | `getBean(UserService.class).getUserRepository() != null`                 |
| Then  | `getBean(UserService.class).getUserRepository() instanceof UserRepository` |
| Then  | `getBean(UserService.class).getUserRepository() == getBean(UserRepository.class)` — 注入的是同一个 singleton 实例 |

### TC-4.2 多层依赖注入（A → B → C）

| 维度  | 描述                                                                     |
|-------|--------------------------------------------------------------------------|
| Given | `ServiceA` 依赖 `ServiceB`，`ServiceB` 依赖 `ServiceC`，三者均 `@Component` |
| When  | 容器启动完成                                                              |
| Then  | `ServiceA.serviceB != null`                                               |
| Then  | `ServiceA.serviceB.serviceC != null`                                      |
| Then  | `ServiceA.serviceB == getBean(ServiceB.class)`                            |
| Then  | `ServiceA.serviceB.serviceC == getBean(ServiceC.class)`                   |

### TC-4.3 无依赖的 Bean

| 维度  | 描述                                                       |
|-------|-----------------------------------------------------------|
| Given | `SimpleComponent` 标注 `@Component`，无任何 `@Autowired` 字段 |
| When  | 容器启动完成                                                |
| Then  | `getBean(SimpleComponent.class) != null`                    |

### TC-4.4 同类型多 Bean — 注入歧义

| 维度  | 描述                                                                       |
|-------|----------------------------------------------------------------------------|
| Given | 接口 `DataSource` 有两个 `@Component` 实现类 `MysqlDataSource`、`H2DataSource` |
| Given | `SomeService` 有字段 `@Autowired DataSource dataSource`                     |
| When  | 容器启动                                                                    |
| Then  | 抛出 `BeansException`                                                       |
| Then  | 异常 message 包含类型名和发现的 Bean 数量                                    |

---

## 5. 循环依赖检测

### TC-5.1 直接循环依赖（A ↔ B）

| 维度  | 描述                                                                  |
|-------|-----------------------------------------------------------------------|
| Given | `CircularA` 有字段 `@Autowired CircularB`                             |
| Given | `CircularB` 有字段 `@Autowired CircularA`                             |
| When  | 容器启动                                                               |
| Then  | 抛出 `BeanCurrentlyInCreationException`                                |
| Then  | 异常 message 包含 `"circularA -> circularB -> circularA"` 或 `"circularB -> circularA -> circularB"` |

### TC-5.2 间接循环依赖（A → B → C → A）

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `CycleA` → `CycleB` → `CycleC` → `CycleA`（三者通过 `@Autowired` 字段互相引用） |
| When  | 容器启动                                                                       |
| Then  | 抛出 `BeanCurrentlyInCreationException`                                        |
| Then  | 异常 message 包含完整依赖链路（如 `"cycleA -> cycleB -> cycleC -> cycleA"`）     |

### TC-5.3 自依赖

| 维度  | 描述                                                        |
|-------|-------------------------------------------------------------|
| Given | `SelfDependent` 有字段 `@Autowired SelfDependent self`       |
| When  | 容器启动                                                     |
| Then  | 抛出 `BeanCurrentlyInCreationException`                      |
| Then  | 异常 message 包含 `"selfDependent -> selfDependent"`          |

### TC-5.4 非循环多依赖 — 正常启动

| 维度  | 描述                                                           |
|-------|----------------------------------------------------------------|
| Given | `ServiceA` → `ServiceB`、`ServiceA` → `ServiceC`，`ServiceB` 和 `ServiceC` 无互相依赖 |
| When  | 容器启动                                                        |
| Then  | 启动成功，不抛出异常                                             |
| Then  | `ServiceA` 的两个 `@Autowired` 字段均已正确注入                  |

---

## 6. 异常信息质量

### TC-6.1 NoSuchBeanDefinitionException 信息完整性

| 维度  | 描述                                                          |
|-------|---------------------------------------------------------------|
| Given | 容器中无名为 `"fooBar"` 的 Bean                                |
| When  | 调用 `getBean("fooBar")`                                       |
| Then  | 异常类型为 `NoSuchBeanDefinitionException`                      |
| Then  | message 包含请求的 beanName `"fooBar"`                          |

### TC-6.2 BeanCurrentlyInCreationException 依赖链完整

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | A ↔ B 循环依赖                                                          |
| When  | 容器启动失败                                                             |
| Then  | 异常 message 中依赖链格式为 `"beanNameA -> beanNameB -> beanNameA"`       |
| Then  | 依赖链中每个节点均使用实际的 beanName（而非类名全路径）                     |

---

## 7. 边界场景

### TC-7.1 refresh 重复调用

| 维度  | 描述                                                            |
|-------|----------------------------------------------------------------|
| Given | `AnnotationConfigApplicationContext` 已完成 `refresh()`         |
| When  | 再次调用 `refresh()`                                            |
| Then  | 抛出 `BeansException` 或幂等处理（二选一，需在实现时确定策略）   |

### TC-7.2 无 @ComponentScan 且无 basePackages

| 维度  | 描述                                                              |
|-------|-------------------------------------------------------------------|
| Given | 构造 `AnnotationConfigApplicationContext` 时未传入任何 basePackages |
| When  | 容器启动                                                           |
| Then  | `getBeanDefinitionCount() == 0`，容器正常启动但无 Bean 注册         |

### TC-7.3 @Autowired 字段类型为接口 — 单一实现

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | 接口 `UserRepository`，唯一实现 `JpaUserRepository` 标注 `@Component`          |
| Given | `UserService` 有字段 `@Autowired UserRepository repo`                          |
| When  | 容器启动完成                                                                   |
| Then  | `UserService.repo` 注入的是 `JpaUserRepository` 实例                           |
