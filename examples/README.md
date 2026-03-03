# Examples

所有可运行示例都在 `examples/src/main/java`，并通过 Maven `exec:java` 直接运行。

## IOC 主线

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1FailurePathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase2.Phase2HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase3.Phase3HappyPathExample
```

## JavaConfig 主线

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase1.JavaConfigPhase1HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase1.JavaConfigPhase1FailurePathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.javaconfig.phase2.JavaConfigPhase2FailurePathExample
```

## Transaction 主线

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.transaction.phase1.TransactionPhase1HappyPathExample
```

## 事务示例输出重点

- `BEGIN_COUNT=2`
- `COMMIT_COUNT=1`
- `ROLLBACK_COUNT=1`
- `PHASE-TRANSACTION-1-HAPPY-PATH: PASS`
