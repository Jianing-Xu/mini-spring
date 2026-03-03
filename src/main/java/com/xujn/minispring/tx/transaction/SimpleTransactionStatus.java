package com.xujn.minispring.tx.transaction;

/**
 * Default transaction status implementation.
 * Constraint: rollback-only may be set for participating transactions to affect outer commit.
 * Thread-safety: single-threaded invocation scope only.
 */
public class SimpleTransactionStatus implements TransactionStatus {

    private final Object resourceKey;
    private final TransactionResource resource;
    private final boolean newTransaction;
    private boolean rollbackOnly;

    public SimpleTransactionStatus(Object resourceKey, TransactionResource resource, boolean newTransaction) {
        this.resourceKey = resourceKey;
        this.resource = resource;
        this.newTransaction = newTransaction;
    }

    public Object getResourceKey() {
        return resourceKey;
    }

    @Override
    public boolean isNewTransaction() {
        return newTransaction;
    }

    @Override
    public boolean isRollbackOnly() {
        return rollbackOnly;
    }

    @Override
    public void setRollbackOnly() {
        this.rollbackOnly = true;
    }

    @Override
    public TransactionResource getResource() {
        return resource;
    }
}
