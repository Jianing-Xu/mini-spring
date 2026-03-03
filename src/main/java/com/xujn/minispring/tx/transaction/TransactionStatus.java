package com.xujn.minispring.tx.transaction;

/**
 * Mutable transaction execution state.
 * Constraint: status is bound to a single invocation and manager key.
 * Thread-safety: not thread-safe.
 */
public interface TransactionStatus {

    boolean isNewTransaction();

    boolean isRollbackOnly();

    void setRollbackOnly();

    TransactionResource getResource();
}
