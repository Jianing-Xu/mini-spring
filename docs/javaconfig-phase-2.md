# JavaConfig Phase 2：参数注入 + 冲突检测 + init/destroy

> **mode**: PHASE  
> **phase_n**: 2  
> **sources**: AnnotationScan + JavaConfig（并存）  
> **conflict_policy**: FAIL_FAST（默认）  
> **javaconfig_features**: CONFIGURATION_CLASS=ENABLED, BEAN_METHOD=ENABLED, BEAN_METHOD_PARAMETER_INJECTION=ENABLED, INIT_DESTROY_METHOD=ENABLED  
> **package group**: `com.xujn`

---

## 1. 目标与范围

### 必须实现

| # | 能力 | 完成标志 |
|---|------|----------|
| 1 | `@Bean` 方法参数按类型注入 | `@Bean A a(B b)` 创建时，参数 `b` 通过 `getBean(B.class)` 解析 |
| 2 | 参数解析失败错误上下文 | 缺失依赖或多候选时，异常信息包含 `beanName`、方法名、参数索引、参数类型 |
| 3 | 重复 beanName FAIL_FAST | AnnotationScan / JavaConfig / 多 `@Configuration` 间同名注册抛出 `BeanDefinitionOverrideException` |
| 4 | 覆盖开关 | `BeanDefinitionRegistry.setAllowOverride(boolean)` 可切换覆盖策略 |
| 5 | `@Bean(initMethod=...)` | Bean 初始化阶段调用自定义 init 方法 |
| 6 | `@Bean(destroyMethod=...)` | `context.close()` 时调用自定义 destroy 方法 |
| 7 | 接口生命周期与自定义方法并存 | `InitializingBean` / `DisposableBean` 与自定义 init/destroy 可同时生效 |

### 不做（Phase 2 边界）

| 排除项 | 说明 |
|--------|------|
| `@Scope` 标注在 `@Bean` 方法上 | 延后，当前 `@Bean` 仍按 singleton 处理 |
| `@Qualifier` / `@Primary` | 参数解析仅按类型 |
| `@Import` | 不在当前阶段 |
| CGLIB `@Configuration` 增强 | 不在当前阶段 |
| 工厂方法 Bean 的三级缓存 / AOP 提前代理协同 | 延后到 JavaConfig Phase 3 |

---

## 2. 核心设计

### 2.1 关键数据结构增量

#### `BeanDefinition`

新增字段：

- `Class<?>[] factoryMethodParameterTypes`
- `String initMethodName`
- `String destroyMethodName`

用途：

- 工厂方法反射定位与参数解析
- JavaConfig 自定义生命周期方法注册

#### `BeanMethod`

新增字段：

- `Class<?>[] parameterTypes`
- `String initMethodName`
- `String destroyMethodName`

#### `BeanDefinitionRegistry`

新增接口：

- `void setAllowOverride(boolean allowOverride)`
- `boolean isAllowOverride()`

#### `BeanDefinitionOverrideException`

职责：

- 重复 beanName 且覆盖关闭时快速失败
- message 必须包含：
  - 冲突 beanName
  - existing source
  - new source

### 2.2 参数解析策略

`AutowireCapableBeanFactory#createBeanByFactoryMethod` 中：

1. 根据 `factoryMethodParameterTypes` 定位工厂方法
2. 逐个按类型调用 `getBean(parameterType)`
3. 解析结果按参数顺序组装 `Object[] args`
4. 调用 `method.invoke(factoryBean, args)`

错误处理：

- 缺失依赖：顶层抛 `BeansException`，根因是 `NoSuchBeanDefinitionException`
- 多候选歧义：顶层抛 `BeansException`
- message 必须包含：
  - 当前 `beanName`
  - 工厂方法名
  - 参数索引
  - 参数类型

### 2.3 冲突策略

默认行为：

- `DefaultListableBeanFactory.allowOverride = false`
- 注册同名 BeanDefinition 时抛出 `BeanDefinitionOverrideException`

覆盖模式：

- 调用 `setAllowOverride(true)` 后，后注册的 BeanDefinition 覆盖先注册的定义
- 当前实现保留原有注册顺序，不重复追加 `beanDefinitionNames`

### 2.4 init / destroy 集成

初始化顺序：

1. `postProcessBeforeInitialization`
2. `InitializingBean.afterPropertiesSet()`
3. 自定义 `initMethod`
4. `postProcessAfterInitialization`

销毁顺序：

1. `DisposableBean.destroy()`
2. 自定义 `destroyMethod`

实现方式：

- 通过 `DisposableBeanAdapter` 统一适配接口销毁与自定义销毁方法
- 若 bean 同时实现 `DisposableBean` 且 `destroyMethod="destroy"`，避免重复调用

---

## 3. 落地类清单

| 类 | 职责 |
|----|------|
| `BeanDefinition` | 保存工厂方法参数、init/destroy 元数据 |
| `BeanMethod` | 保存解析出的参数、init/destroy 元数据 |
| `ConfigurationClassParser` | 从 `@Bean` 注解提取参数与生命周期元数据 |
| `ConfigurationClassBeanDefinitionReader` | 将解析结果写入 `BeanDefinition` |
| `DefaultListableBeanFactory` | 处理重复 beanName 检测与覆盖开关 |
| `AutowireCapableBeanFactory` | 执行参数解析、initMethod 调用、destroy 注册 |
| `DisposableBeanAdapter` | 统一销毁回调 |
| `BeanDefinitionOverrideException` | 冲突异常类型 |

---

## 4. 验收映射

| 验收场景 | 测试入口 | 示例入口 |
|----------|----------|----------|
| 参数按类型注入成功 | `JavaConfigPhase2AcceptanceTest#shouldResolveBeanMethodParametersByType` | `JavaConfigPhase2HappyPathExample` |
| 参数缺失失败 | `JavaConfigPhase2AcceptanceTest#shouldFailWhenBeanMethodParameterTypeIsMissing` | `JavaConfigPhase2FailurePathExample` |
| 参数歧义失败 | `JavaConfigPhase2AcceptanceTest#shouldFailWhenBeanMethodParameterTypeIsAmbiguous` | 无 |
| AnnotationScan 与 JavaConfig 同名冲突 | `JavaConfigPhase2AcceptanceTest#shouldFailFastForDuplicateBeanNamesAcrossSources` | `JavaConfigPhase2FailurePathExample` |
| 自定义 init/destroy 生效 | `JavaConfigPhase2AcceptanceTest#shouldInvokeCustomInitAndDestroyMethodsForBeanMethods` | `JavaConfigPhase2HappyPathExample` |

---

## 5. 当前遗留项

- `@Scope("prototype")` 标注在 `@Bean` 方法上尚未支持
- 工厂方法 Bean 参与三级缓存循环依赖尚未专门验收
- 工厂方法 Bean 的 AOP 提前代理一致性尚未专门验收
