# JavaConfig Phase 1 验收用例清单

> **phase_n**: 1  
> **sources**: AnnotationScan + JavaConfig（并存）  
> **conflict_policy**: FAIL_FAST（Phase 2 落地）  
> **覆盖策略**：正常路径 + 失败路径 + 并存场景

---

## 1. @Configuration 类扫描与注册

### TC-1.1 @Configuration 类被 AnnotationScan 注册

| 维度  | 描述                                                                 |
|-------|----------------------------------------------------------------------|
| Given | `AppConfig` 标注 `@Configuration`（内含 `@Component` 元标注）          |
| When  | `AnnotationConfigApplicationContext` 扫描 `AppConfig` 所在包           |
| Then  | `containsBeanDefinition("appConfig") == true`                         |
| Then  | `getBean(AppConfig.class)` 返回非 null                                |

### TC-1.2 @Configuration 类作为 singleton 实例

| 维度  | 描述                                                                |
|-------|---------------------------------------------------------------------|
| Given | `AppConfig` 标注 `@Configuration`                                    |
| When  | 多次调用 `getBean(AppConfig.class)`                                   |
| Then  | `assertSame` — 返回同一实例                                          |

### TC-1.3 非 @Configuration 类不被解析

| 维度  | 描述                                                                |
|-------|---------------------------------------------------------------------|
| Given | `PlainComponent` 标注 `@Component`（非 `@Configuration`），内有 `@Bean` 方法 |
| When  | 容器启动                                                             |
| Then  | `PlainComponent` 中的 `@Bean` 方法不被解析为 BeanDefinition              |
| Then  | `containsBeanDefinition("<beanMethodName>") == false`                 |

---

## 2. @Bean 方法解析与 BeanDefinition 注册

### TC-2.1 无参 @Bean 方法注册为 BD

| 维度  | 描述                                                                           |
|-------|--------------------------------------------------------------------------------|
| Given | `AppConfig` 中有 `@Bean DataSource dataSource() { return new HikariDS(); }`     |
| When  | 容器启动                                                                        |
| Then  | `containsBeanDefinition("dataSource") == true`                                  |
| Then  | `getBeanDefinition("dataSource").getFactoryBeanName().equals("appConfig")`       |
| Then  | `getBeanDefinition("dataSource").getFactoryMethodName().equals("dataSource")`    |

### TC-2.2 @Bean(name="custom") 自定义名称

| 维度  | 描述                                                                                |
|-------|------------------------------------------------------------------------------------|
| Given | `AppConfig` 中有 `@Bean(name="myDS") DataSource dataSource() { ... }`               |
| When  | 容器启动                                                                             |
| Then  | `containsBeanDefinition("myDS") == true`                                             |
| Then  | `containsBeanDefinition("dataSource") == false`                                      |
| Then  | `getBean("myDS")` 返回正确实例                                                       |

### TC-2.3 多个 @Bean 方法注册

| 维度  | 描述                                                              |
|-------|-------------------------------------------------------------------|
| Given | `AppConfig` 中有 3 个 `@Bean` 方法：`serviceA()`、`serviceB()`、`serviceC()` |
| When  | 容器启动                                                           |
| Then  | 3 个 BD 均已注册：`containsBeanDefinition` 返回 true                |
| Then  | 3 个 Bean 均可通过 `getBean` 获取                                   |

### TC-2.4 多个 @Configuration 类

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `AppConfig1` 和 `AppConfig2` 各自标注 `@Configuration`，各含不同 @Bean 方法          |
| When  | 容器启动                                                                           |
| Then  | 两个配置类中的所有 @Bean 方法均被解析注册                                             |

### TC-2.5 非 @Bean 方法不注册

| 维度  | 描述                                                            |
|-------|-----------------------------------------------------------------|
| Given | `AppConfig` 中有普通方法 `helper()`（未标注 @Bean）               |
| When  | 容器启动                                                         |
| Then  | `containsBeanDefinition("helper") == false`                      |

---

## 3. 工厂方法创建 Bean

### TC-3.1 工厂方法创建实例

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | `@Bean DataSource dataSource()` 返回 `new HikariDataSource()`           |
| When  | 调用 `getBean(DataSource.class)`                                         |
| Then  | 返回非 null 且 `instanceof DataSource`                                  |

### TC-3.2 singleton 一致性

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | `@Bean DataSource dataSource()` 注册的 BD scope 默认为 singleton         |
| When  | 调用 `getBean(DataSource.class)` 两次                                    |
| Then  | `assertSame` — 同一实例                                                 |

### TC-3.3 @Bean 方法返回 null

| 维度  | 描述                                                                    |
|-------|-------------------------------------------------------------------------|
| Given | `@Bean Object nullBean() { return null; }`                               |
| When  | 容器启动（触发 getBean）                                                 |
| Then  | 抛出 `BeansException`                                                    |
| Then  | message 包含 `"@Bean method returned null"` 和方法名                      |

### TC-3.4 工厂方法创建的 Bean 走完整生命周期

| 维度  | 描述                                                                                |
|-------|-------------------------------------------------------------------------------------|
| Given | `@Bean LifecycleService lifecycleService()`，`LifecycleService` 实现 `InitializingBean` |
| Given | 容器已注册 BPP                                                                       |
| When  | 容器启动                                                                              |
| Then  | `afterPropertiesSet()` 被调用                                                         |
| Then  | BPP 的 `postProcessBeforeInitialization` / `postProcessAfterInitialization` 对该 Bean 生效 |

### TC-3.5 工厂方法创建的 Bean 支持 @Autowired 字段注入

| 维度  | 描述                                                                                            |
|-------|------------------------------------------------------------------------------------------------|
| Given | `@Bean ServiceA serviceA()` 返回的 `ServiceA` 有 `@Autowired ServiceB serviceB` 字段            |
| Given | `ServiceB` 通过 `@Component` 注册                                                               |
| When  | 容器启动                                                                                        |
| Then  | `getBean(ServiceA.class).getServiceB() != null` — @Autowired 字段在 populateBean 阶段被注入      |

---

## 4. AnnotationScan + JavaConfig 并存

### TC-4.1 两种配置源 Bean 共存

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `UserService` 通过 `@Component` 注册；`DataSource` 通过 `@Bean` 注册               |
| When  | 容器启动                                                                           |
| Then  | `getBean(UserService.class)` 和 `getBean(DataSource.class)` 均返回正确实例          |

### TC-4.2 JavaConfig Bean 依赖 @Component Bean（通过 @Autowired 字段注入）

| 维度  | 描述                                                                                   |
|-------|----------------------------------------------------------------------------------------|
| Given | `@Bean MyService myService()` 返回的 `MyService` 有 `@Autowired UserRepository repo`   |
| Given | `UserRepository` 通过 `@Component` 注册                                                 |
| When  | 容器启动                                                                                |
| Then  | `MyService.repo` 已注入 `UserRepository` 实例                                           |

### TC-4.3 @Component Bean 依赖 JavaConfig Bean

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `UserService`（@Component）有 `@Autowired DataSource ds`                           |
| Given | `DataSource` 通过 `@Bean` 注册                                                     |
| When  | 容器启动                                                                           |
| Then  | `UserService.ds` 已注入 @Bean 创建的 `DataSource` 实例                              |

---

## 5. ConfigurationClassPostProcessor

### TC-5.1 BFPP 在 Bean 实例化前执行

| 维度  | 描述                                                                              |
|-------|-----------------------------------------------------------------------------------|
| Given | `AppConfig` 中有 `@Bean ServiceX serviceX()`                                       |
| When  | 容器启动                                                                           |
| Then  | `serviceX` BD 在 `preInstantiateSingletons` 之前已注册（BFPP 阶段完成注册）         |

### TC-5.2 无 @Configuration 类 — BFPP 跳过

| 维度  | 描述                                                           |
|-------|----------------------------------------------------------------|
| Given | 扫描包中仅有 `@Component` 类，无 `@Configuration` 类            |
| When  | 容器启动                                                        |
| Then  | `ConfigurationClassPostProcessor` 执行但无配置类需要解析          |
| Then  | 容器正常启动，无异常                                              |

---

## 6. 异常与错误信息

### TC-6.1 @Bean 方法返回 void

| 维度  | 描述                                                         |
|-------|--------------------------------------------------------------|
| Given | `@Bean void invalidBean() { ... }`（返回类型为 void）         |
| When  | ConfigurationClassParser 解析该方法                            |
| Then  | 跳过该方法（void 返回类型不生成 BD）或抛出明确异常              |

### TC-6.2 工厂方法反射调用失败

| 维度  | 描述                                                                          |
|-------|-------------------------------------------------------------------------------|
| Given | `@Bean DataSource dataSource()` 方法体内抛出 `RuntimeException("init failed")` |
| When  | `getBean("dataSource")` 触发工厂方法调用                                       |
| Then  | 抛出 `BeansException`                                                          |
| Then  | message 包含工厂方法名 `"dataSource"` 和原始异常信息                             |

### TC-6.3 factoryBeanName 对应的 Bean 不存在

| 维度  | 描述                                                                                |
|-------|-------------------------------------------------------------------------------------|
| Given | BD 的 factoryBeanName 指向一个不存在的 beanName（异常配置场景）                       |
| When  | `getBean` 触发 `getBean(factoryBeanName)`                                            |
| Then  | 抛出 `NoSuchBeanDefinitionException`                                                 |
| Then  | message 包含 factoryBeanName                                                         |

---

## 7. WARN 日志

### TC-7.1 CGLIB 未增强警告

| 维度  | 描述                                                                            |
|-------|---------------------------------------------------------------------------------|
| Given | 存在 `@Configuration` 类                                                         |
| When  | 容器启动                                                                         |
| Then  | 输出 WARN 级别日志，包含配置类名和 `"not CGLIB-enhanced"` 关键字                  |
