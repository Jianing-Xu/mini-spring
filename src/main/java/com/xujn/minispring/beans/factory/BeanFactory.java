package com.xujn.minispring.beans.factory;

/**
 * Root bean lookup contract for the mini container.
 * Constraint: implementations must support lookup by name and by type.
 * Thread-safety: depends on implementation.
 */
public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(String name, Class<T> requiredType);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);
}
