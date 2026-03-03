package com.xujn.minispring.tx.transaction;

/**
 * Top-level local transaction manager contract.
 * Constraint: MVP semantics are local-thread-bound and REQUIRED only.
 * Thread-safety: implementations depend on underlying resource management.
 */
public interface PlatformTransactionManager {

    TransactionStatus getTransaction(TransactionDefinition definition);

    void commit(TransactionStatus status);

    void rollback(TransactionStatus status);
}
