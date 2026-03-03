# mini-spring

`mini-spring` 是一个按阶段实现的轻量 Spring 核心机制练习项目，当前已经覆盖：

- IOC 容器、`@Component` 扫描、字段注入
- singleton / prototype、生命周期回调、`BeanPostProcessor`
- JDK 动态代理 AOP、自动代理创建器
- 三级缓存循环依赖解决
- JavaConfig：`@Configuration`、`@Bean`、参数注入、重复 beanName 冲突检测、`initMethod` / `destroyMethod`

## 环境要求

- JDK 17
- Maven 3.9+

## 快速开始

编译并执行全部测试：

```bash
mvn test
```

仅编译源码：

```bash
mvn -q -DskipTests compile
```

## 项目结构

```text
docs/                      设计文档
tests/                     验收文档
src/main/java/com/xujn/minispring
  aop/                     AOP 核心实现
  beans/                   Bean 元数据、工厂、注册表
  context/                 ApplicationContext 与注解配置入口
  core/                    通用注解/反射工具
  exception/               容器异常体系
src/test/java/             单元测试与阶段验收测试
examples/                  可运行示例
```

## 当前实现状态

### IOC 主线

- Phase 1：AnnotationScan、`getBean`、字段注入、基础错误信息
- Phase 2：生命周期、`BeanPostProcessor`、prototype、JDK Proxy AOP
- Phase 3：三级缓存、early reference、AOP 提前代理一致性

### JavaConfig 主线

- Phase 1：`@Configuration` / `@Bean` 解析、工厂方法 Bean 创建
- Phase 2：`@Bean` 参数按类型注入、重复 beanName FAIL_FAST、`initMethod` / `destroyMethod`

### 尚未实现

- JavaConfig Phase 3：工厂方法 Bean 的三级缓存与 AOP 协同验收闭环
- `@Scope` 标注在 `@Bean` 方法上
- `@Import`
- CGLIB `@Configuration` 增强
- `@Qualifier` / `@Primary`

## 示例运行

### IOC 主线

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1FailurePathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase2.Phase2HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase3.Phase3HappyPathExample
```

### JavaConfig 主线

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase1.JavaConfigPhase1HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase1.JavaConfigPhase1FailurePathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2FailurePathExample
```

## 文档索引

### 主线架构

- [architecture.md](/Users/xjn/Develop/projects/java/mini-spring/docs/architecture.md)
- [phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/docs/phase-1.md)
- [phase-2.md](/Users/xjn/Develop/projects/java/mini-spring/docs/phase-2.md)
- [phase-3.md](/Users/xjn/Develop/projects/java/mini-spring/docs/phase-3.md)

### JavaConfig

- [architecture-javaconfig.md](/Users/xjn/Develop/projects/java/mini-spring/docs/architecture-javaconfig.md)
- [javaconfig-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/docs/javaconfig-phase-1.md)
- [javaconfig-phase-2.md](/Users/xjn/Develop/projects/java/mini-spring/docs/javaconfig-phase-2.md)

### 三级缓存专项

- [architecture-three-level-cache.md](/Users/xjn/Develop/projects/java/mini-spring/docs/architecture-three-level-cache.md)
- [three-level-cache-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/docs/three-level-cache-phase-1.md)

### 验收文档

- [acceptance-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-phase-1.md)
- [acceptance-phase-2.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-phase-2.md)
- [acceptance-phase-3.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-phase-3.md)
- [acceptance-javaconfig-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-javaconfig-phase-1.md)
- [acceptance-javaconfig-phase-2.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-javaconfig-phase-2.md)

## 测试入口

关键验收测试：

- [Phase1AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/Phase1AcceptanceTest.java)
- [Phase2AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/Phase2AcceptanceTest.java)
- [Phase3AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/Phase3AcceptanceTest.java)
- [JavaConfigPhase1AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/JavaConfigPhase1AcceptanceTest.java)
- [JavaConfigPhase2AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/JavaConfigPhase2AcceptanceTest.java)
