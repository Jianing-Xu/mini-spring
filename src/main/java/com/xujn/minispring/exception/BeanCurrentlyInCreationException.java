package com.xujn.minispring.exception;

/**
 * Raised when Phase 1 detects a circular dependency while creating a singleton.
 * Constraint: message must contain bean name and the full dependency chain.
 * Thread-safety: immutable exception type.
 */
public class BeanCurrentlyInCreationException extends BeansException {

    public BeanCurrentlyInCreationException(String beanName, String dependencyChain) {
        super("Bean '" + beanName + "' is currently in creation: " + dependencyChain);
    }

    public BeanCurrentlyInCreationException(String beanName, String dependencyChain, String detail) {
        super("Bean '" + beanName + "' is currently in creation: " + dependencyChain + " - " + detail);
    }
}
