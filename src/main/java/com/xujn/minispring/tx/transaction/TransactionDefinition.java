package com.xujn.minispring.tx.transaction;

/**
 * Static transaction definition contract.
 * Constraint: MVP supports only REQUIRED propagation and default rollback rules.
 * Thread-safety: implementations should be immutable.
 */
public interface TransactionDefinition {

    int PROPAGATION_REQUIRED = 0;

    default int getPropagationBehavior() {
        return PROPAGATION_REQUIRED;
    }

    default boolean rollbackOn(Throwable ex) {
        return ex instanceof RuntimeException || ex instanceof Error;
    }
}
