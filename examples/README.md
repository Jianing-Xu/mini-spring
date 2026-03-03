# Examples

Phase 1 runnable examples live under `examples/src/main/java`.

Run them with Maven:

```bash
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1HappyPathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase1.Phase1FailurePathExample
mvn -q -DskipTests compile exec:java -Dexec.mainClass=com.xujn.minispring.examples.phase2.Phase2HappyPathExample
```
