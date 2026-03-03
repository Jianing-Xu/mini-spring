# Examples

`examples/` 现在是独立 Maven 工程，所有可运行示例都在 `examples/src/main/java`，通过 `examples/pom.xml` 单独编译和运行。

先在仓库根目录执行：

```bash
mvn test
```

## IOC 主线

```bash
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1HappyPathExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1FailurePathExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase2.Phase2HappyPathExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase3.Phase3HappyPathExample
```

## JavaConfig 主线

```bash
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase1.JavaConfigPhase1HappyPathExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase1.JavaConfigPhase1FailurePathExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2HappyPathExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2FailurePathExample
```

## Transaction 主线

```bash
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.transaction.phase1.TransactionPhase1HappyPathExample
```

## MVC 主线

```bash
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispringmvc.examples.phase1.Phase1DispatcherExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispringmvc.examples.phase2.Phase2BindingExample
mvn -q -f examples/pom.xml -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispringmvc.examples.phase3.Phase3MvcExample
```

## 事务示例输出重点

- `BEGIN_COUNT=2`
- `COMMIT_COUNT=1`
- `ROLLBACK_COUNT=1`
- `PHASE-TRANSACTION-1-HAPPY-PATH: PASS`

## MVC 示例输出重点

- Phase 1：`PHASE-MVC-1-HAPPY-PATH: PASS`
- Phase 2：`PHASE-MVC-2-HAPPY-PATH: PASS`
- Phase 3：`INTERCEPTOR_BODY=view:ok`
- Phase 3：`VIEW_BODY=view:userDetail|name=alice`
- Phase 3：`SHORT_STATUS=403`
- Phase 3：`PHASE-MVC-3-HAPPY-PATH: PASS`
