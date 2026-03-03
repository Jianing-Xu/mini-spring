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
    public static final String SCOPE_PROTOTYPE = "prototype";

    private final Class<?> beanClass;
    private final String beanName;
    private String scope = SCOPE_SINGLETON;
    private final List<PropertyValue> propertyValues = new ArrayList<>();
    private boolean lazyInit;
    private String factoryBeanName;
    private String factoryMethodName;
    private String source;

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

    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public boolean isFactoryMethod() {
        return factoryBeanName != null && factoryMethodName != null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
