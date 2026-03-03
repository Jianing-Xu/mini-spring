package com.xujn.minispring.beans.factory.config;

import com.xujn.minispring.beans.PropertyValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Metadata description for a bean managed by the container.
 * Constraint: Phase 1 only supports singleton scope.
 * Thread-safety: mutable during bootstrap only, not intended for concurrent mutation.
 */
public class BeanDefinition {

    public static final String SCOPE_SINGLETON = "singleton";

    private final Class<?> beanClass;
    private final String beanName;
    private String scope = SCOPE_SINGLETON;
    private final List<PropertyValue> propertyValues = new ArrayList<>();
    private boolean lazyInit;

    public BeanDefinition(Class<?> beanClass, String beanName) {
        this.beanClass = Objects.requireNonNull(beanClass, "beanClass must not be null");
        this.beanName = Objects.requireNonNull(beanName, "beanName must not be null");
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = Objects.requireNonNull(scope, "scope must not be null");
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }
}
