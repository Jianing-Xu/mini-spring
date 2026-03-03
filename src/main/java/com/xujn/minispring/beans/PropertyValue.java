package com.xujn.minispring.beans;

/**
 * Placeholder metadata for future property injection expansion.
 * Constraint: retained in Phase 1 for BeanDefinition completeness only.
 * Thread-safety: immutable value object.
 */
public class PropertyValue {

    private final String name;
    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
