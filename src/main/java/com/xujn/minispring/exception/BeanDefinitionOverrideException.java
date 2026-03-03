package com.xujn.minispring.exception;

/**
 * Raised when a bean definition is registered with a duplicate name while overriding is disabled.
 * Constraint: message must include bean name plus both existing and incoming definition sources.
 * Thread-safety: immutable exception type.
 */
public class BeanDefinitionOverrideException extends BeansException {

    public BeanDefinitionOverrideException(String beanName, String existingSource, String newSource) {
        super("BeanDefinition named '" + beanName + "' already exists; existing source='" +
                existingSource + "', new source='" + newSource + "'");
    }
}
