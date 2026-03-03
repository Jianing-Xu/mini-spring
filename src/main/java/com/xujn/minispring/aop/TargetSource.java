package com.xujn.minispring.aop;

/**
 * Holds the target object and the interfaces exposed by its proxy.
 * Constraint: Phase 2 only supports JDK proxies, so interfaces are mandatory for proxy creation.
 * Thread-safety: immutable value object.
 */
public class TargetSource {

    private final Object target;
    private final Class<?>[] targetInterfaces;

    public TargetSource(Object target, Class<?>[] targetInterfaces) {
        this.target = target;
        this.targetInterfaces = targetInterfaces;
    }

    public Object getTarget() {
        return target;
    }

    public Class<?>[] getTargetInterfaces() {
        return targetInterfaces;
    }
}
