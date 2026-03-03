package com.xujn.minispring.tx.transaction;

/**
 * Minimal transactional resource abstraction used by the MVP manager.
 * Constraint: resource lifecycle is local to one thread-bound transaction at a time.
 * Thread-safety: depends on implementation.
 */
public interface TransactionResource {

    void begin();

    void commit();

    void rollback();

    boolean isActive();
}
