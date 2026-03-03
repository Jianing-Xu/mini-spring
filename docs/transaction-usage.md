# Transaction Usage

## 目标

这份文档描述当前 `mini-spring` 事务 MVP 的接入方式、运行边界和调试口径。它只对应：

- [architecture-transaction.md](/Users/xjn/Develop/projects/java/mini-spring/docs/architecture-transaction.md)
- [transaction-phase-1.md](/Users/xjn/Develop/projects/java/mini-spring/docs/transaction-phase-1.md)

## 已支持能力

- `@Transactional`
- 默认传播行为 `REQUIRED`
- 线程绑定事务上下文
- 外层开启事务、内层参与事务
- 运行时异常回滚
- 正常执行提交
- 类级、方法级注解解析

## 未支持能力

- `REQUIRES_NEW`
- `NESTED`
- `rollbackFor` / `noRollbackFor`
- 无接口 Bean 的事务代理
- 自调用事务增强
- 真实 JDBC `Connection` 绑定

## 使用前提

事务增强建立在现有 JDK 动态代理之上，因此必须满足：

- 目标 Bean 存在接口
- 容器中存在唯一的 `PlatformTransactionManager` Bean
- 业务方法通过容器获取到的代理对象调用

如果目标 Bean 没有接口，容器会跳过事务代理，并打印警告。

## 最小接入示例

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
    @Override
    public void submit(boolean fail) {
        if (fail) {
            throw new IllegalStateException("rollback");
        }
    }
}
```

启动与调用：

```java
AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext("com.xujn.minispring.examples.transaction.phase1.fixture");

OrderService orderService = context.getBean(OrderService.class);
orderService.submit(false);
```

## 事务行为说明

### 提交

- 方法进入时若线程内不存在事务，事务管理器创建新事务
- 方法正常返回时提交事务
- 事务完成后清理线程上下文

### 回滚

- 新事务内抛出运行时异常时回滚
- 已参与外层事务的方法抛出运行时异常时，标记当前线程事务为 rollback-only
- 外层事务在提交前检测到 rollback-only，会转为回滚

### 注解优先级

- 方法级 `@Transactional` 高于类级 `@Transactional`
- 如果方法无注解，则回退到实现类级注解

## 调试入口

验收测试：

- [TransactionPhase1AcceptanceTest.java](/Users/xjn/Develop/projects/java/mini-spring/src/test/java/com/xujn/minispring/context/TransactionPhase1AcceptanceTest.java)

手工示例：

- [TransactionPhase1HappyPathExample.java](/Users/xjn/Develop/projects/java/mini-spring/examples/src/main/java/com/xujn/minispring/examples/transaction/phase1/TransactionPhase1HappyPathExample.java)

运行命令：

```bash
mvn test -Dtest=TransactionPhase1AcceptanceTest
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.transaction.phase1.TransactionPhase1HappyPathExample
```
