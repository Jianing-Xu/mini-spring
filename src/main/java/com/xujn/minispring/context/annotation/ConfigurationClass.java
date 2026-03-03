package com.xujn.minispring.context.annotation;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Metadata model for a discovered {@link Configuration} class.
 * Constraint: bean methods preserve declaration order for deterministic registration.
 * Thread-safety: mutable only during JavaConfig parsing.
 */
public class ConfigurationClass {

    private final Class<?> configClass;
    private final String beanName;
    private final Set<BeanMethod> beanMethods = new LinkedHashSet<>();

    public ConfigurationClass(Class<?> configClass, String beanName) {
        this.configClass = Objects.requireNonNull(configClass, "configClass must not be null");
        this.beanName = Objects.requireNonNull(beanName, "beanName must not be null");
    }

    public Class<?> getConfigClass() {
        return configClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public Set<BeanMethod> getBeanMethods() {
        return beanMethods;
    }
}
