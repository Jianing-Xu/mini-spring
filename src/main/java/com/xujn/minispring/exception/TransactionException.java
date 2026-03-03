package com.xujn.minispring.exception;

/**
 * Base runtime exception for transaction subsystem failures.
 * Constraint: messages should include the failing transactional method or resource context.
 * Thread-safety: immutable exception type.
 */
public class TransactionException extends BeansException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
