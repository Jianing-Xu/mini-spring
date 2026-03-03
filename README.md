# mini-spring

`mini-spring` 是一个按阶段实现的轻量 Spring 核心机制练习项目，当前已经覆盖：

- IOC 容器、`@Component` 扫描、字段注入
- singleton / prototype、生命周期回调、`BeanPostProcessor`
- JDK 动态代理 AOP、自动代理创建器
- 三级缓存循环依赖解决
- JavaConfig：`@Configuration`、`@Bean`、参数注入、重复 beanName 冲突检测、`initMethod` / `destroyMethod`
- 事务 MVP：`@Transactional`、线程绑定事务上下文、`REQUIRED` 传播、提交与回滚

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
  tx/                      事务注解、拦截器、事务管理器、线程上下文
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

### Transaction 主线

- Phase 1：`@Transactional`、`REQUIRED`、类级/方法级注解解析、线程绑定事务上下文、提交/回滚

### 尚未实现

- JavaConfig Phase 3：工厂方法 Bean 的三级缓存与 AOP 协同验收闭环
- `@Scope` 标注在 `@Bean` 方法上
- `@Import`
- CGLIB `@Configuration` 增强
- `@Qualifier` / `@Primary`
- 事务传播行为扩展：`REQUIRES_NEW`、`NESTED`
- 回滚规则扩展：`rollbackFor` / `noRollbackFor`
- 真实 JDBC `Connection` 集成与声明式数据源装配

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

### Transaction 主线

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.transaction.phase1.TransactionPhase1HappyPathExample
```

## 事务使用方式

最小接入要求：

- 业务 Bean 需要有接口，当前事务代理基于 JDK 动态代理
- 容器内需要存在一个 `PlatformTransactionManager` Bean
- 在接口实现类或具体方法上标注 `@Transactional`

最小示例：

```java
@Component
public class RecordingTransactionManager extends DataSourceTransactionManager {
    public RecordingTransactionManager() {
        super(new RecordingTransactionResourceFactory());
    }
}

public interface OrderService {
    void submit(boolean fail);
}

@Component
public class OrderServiceImpl implements OrderService {

    @Transactional
    public void submit(boolean fail) {
        if (fail) {
            throw new IllegalStateException("rollback");
        }
    }
}
```

当前事务语义：

- 默认传播行为为 `REQUIRED`
- 运行时异常触发回滚
- 同线程嵌套调用会参与已有事务
- 自调用不生效
- 无接口 Bean 不创建事务代理

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

### 事务专项

- [architecture-transaction.md](/Users/xjn/Develop/projects/java/mini-spring/docs/architecture-transaction.md)
- [transaction-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/docs/transaction-phase-1.md)
- [transaction-usage.md](/Users/xjn/Develop/projects/java/mini-spring/docs/transaction-usage.md)

### 验收文档

- [acceptance-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-phase-1.md)
- [acceptance-phase-2.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-phase-2.md)
- [acceptance-phase-3.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-phase-3.md)
- [acceptance-javaconfig-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-javaconfig-phase-1.md)
- [acceptance-javaconfig-phase-2.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-javaconfig-phase-2.md)
- [acceptance-transaction-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/tests/acceptance-transaction-phase-1.md)

## 测试入口

关键验收测试：

- [Phase1AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/Phase1AcceptanceTest.java)
- [Phase2AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/Phase2AcceptanceTest.java)
- [Phase3AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/Phase3AcceptanceTest.java)
- [JavaConfigPhase1AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/JavaConfigPhase1AcceptanceTest.java)
- [JavaConfigPhase2AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/JavaConfigPhase2AcceptanceTest.java)
- [TransactionPhase1AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/TransactionPhase1AcceptanceTest.java)
