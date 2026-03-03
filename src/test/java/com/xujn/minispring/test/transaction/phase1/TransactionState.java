package com.xujn.minispring.test.transaction.phase1;

public final class TransactionState {

    public static int beginCount;
    public static int commitCount;
    public static int rollbackCount;

    private TransactionState() {
    }

    public static void reset() {
        beginCount = 0;
        commitCount = 0;
        rollbackCount = 0;
    }
}
