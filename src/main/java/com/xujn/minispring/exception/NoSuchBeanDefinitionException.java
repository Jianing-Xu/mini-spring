package com.xujn.minispring.exception;

/**
 * Raised when a bean cannot be resolved by name or type.
 * Constraint: message must include the missing bean name or required type.
 * Thread-safety: immutable exception type.
 */
public class NoSuchBeanDefinitionException extends BeansException {

    public NoSuchBeanDefinitionException(String beanName) {
        super("No bean named '" + beanName + "' is defined");
    }

    public NoSuchBeanDefinitionException(Class<?> requiredType) {
        super("No bean of type [" + requiredType.getName() + "] is defined");
    }
}
