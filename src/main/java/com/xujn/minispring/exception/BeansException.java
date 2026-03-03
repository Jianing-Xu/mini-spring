package com.xujn.minispring.exception;

/**
 * Base runtime exception for all container errors.
 * Constraint: every message must carry enough bean context for diagnosis.
 * Thread-safety: immutable after construction and therefore thread-safe.
 */
public class BeansException extends RuntimeException {

    public BeansException(String message) {
        super(message);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
