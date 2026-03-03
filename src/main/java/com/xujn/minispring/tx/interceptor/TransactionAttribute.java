package com.xujn.minispring.tx.interceptor;

import com.xujn.minispring.tx.transaction.TransactionDefinition;

/**
 * Resolved transaction metadata for a method invocation.
 * Constraint: MVP carries only REQUIRED propagation and default rollback rules.
 * Thread-safety: immutable after construction.
 */
public class TransactionAttribute implements TransactionDefinition {

    private final String descriptor;
    private final String source;

    public TransactionAttribute(String descriptor, String source) {
        this.descriptor = descriptor;
        this.source = source;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public String getSource() {
        return source;
    }
}
