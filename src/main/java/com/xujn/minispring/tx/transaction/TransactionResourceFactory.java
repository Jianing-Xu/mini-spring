package com.xujn.minispring.tx.transaction;

/**
 * Factory for creating new local transaction resources.
 * Constraint: called only when no thread-bound resource exists for the manager.
 * Thread-safety: implementations should be safe for bootstrap/runtime lookups.
 */
public interface TransactionResourceFactory {

    TransactionResource createResource();
}
