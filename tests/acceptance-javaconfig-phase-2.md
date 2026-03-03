# JavaConfig Phase 2 验收用例清单

> **phase_n**: 2  
> **sources**: AnnotationScan + JavaConfig（并存）  
> **conflict_policy**: FAIL_FAST（默认）  
> **覆盖策略**：参数注入 + 冲突检测 + 生命周期回调

---

## 1. @Bean 方法参数注入

### TC-1.1 参数按类型自动解析

| 维度 | 描述 |
|------|------|
| Given | `@Bean ConfigCreatedService configCreatedService(Repository repository)` |
| Given | `Repository` 已通过容器注册 |
| When | 容器启动并创建 `configCreatedService` |
| Then | `repository` 参数由容器自动解析并传入 |

### TC-1.2 参数类型不存在

| 维度 | 描述 |
|------|------|
| Given | `@Bean MissingService missingService(MissingDependency dependency)` |
| Given | `MissingDependency` 不存在于容器 |
| When | 容器启动 |
| Then | 顶层抛出 `BeansException` |
| Then | 根因是 `NoSuchBeanDefinitionException` |
| Then | message 包含 beanName、方法名、参数索引、参数类型 |

### TC-1.3 参数类型存在多个候选

| 维度 | 描述 |
|------|------|
| Given | `@Bean AmbiguousService ambiguousService(Client client)` |
| Given | `Client` 在容器中存在多个实现 |
| When | 容器启动 |
| Then | 抛出 `BeansException` |
| Then | message 包含 beanName、方法名、参数类型 |

---

## 2. 重复 beanName 冲突检测

### TC-2.1 AnnotationScan 与 JavaConfig 同名冲突

| 维度 | 描述 |
|------|------|
| Given | `@Component("duplicateService")` 和 `@Bean(name="duplicateService")` 同时存在 |
| When | 容器启动 |
| Then | 抛出 `BeanDefinitionOverrideException` |
| Then | message 包含冲突 beanName、existing source、new source |

### TC-2.2 默认策略为 FAIL_FAST

| 维度 | 描述 |
|------|------|
| Given | 未显式调用 `setAllowOverride(true)` |
| When | 注册重复 beanName |
| Then | 容器不会静默覆盖旧定义 |

---

## 3. `initMethod` / `destroyMethod`

### TC-3.1 自定义 initMethod 被调用

| 维度 | 描述 |
|------|------|
| Given | `@Bean(initMethod="customInit")` |
| When | Bean 创建完成 |
| Then | `customInit()` 在初始化阶段被调用 |

### TC-3.2 自定义 destroyMethod 被调用

| 维度 | 描述 |
|------|------|
| Given | `@Bean(destroyMethod="customDestroy")` |
| When | `context.close()` |
| Then | `customDestroy()` 被调用 |

### TC-3.3 接口生命周期与自定义方法并存

| 维度 | 描述 |
|------|------|
| Given | Bean 同时实现 `InitializingBean` 和 `DisposableBean`，并声明 `initMethod` / `destroyMethod` |
| When | 容器启动并关闭 |
| Then | `afterPropertiesSet()` 与 `customInit()` 均执行 |
| Then | `destroy()` 与 `customDestroy()` 均执行 |

---

## 4. 手工验收入口

| 类型 | 入口 |
|------|------|
| happy path | `com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2HappyPathExample` |
| failure path | `com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2FailurePathExample` |
