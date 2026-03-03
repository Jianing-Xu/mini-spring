package com.xujn.minispring.test.transaction.phase1;

import com.xujn.minispring.tx.transaction.TransactionResource;

public class RecordingTransactionResource implements TransactionResource {

    private boolean active;

    @Override
    public void begin() {
        active = true;
        TransactionState.beginCount++;
    }

    @Override
    public void commit() {
        active = false;
        TransactionState.commitCount++;
    }

    @Override
    public void rollback() {
        active = false;
        TransactionState.rollbackCount++;
    }

    @Override
    public boolean isActive() {
        return active;
    }
}
