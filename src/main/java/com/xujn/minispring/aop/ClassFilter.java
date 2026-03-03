package com.xujn.minispring.aop;

/**
 * Matches whether a target class should be considered by a pointcut.
 * Constraint: Phase 2 uses class filtering based on execution() package patterns.
 * Thread-safety: implementations should be immutable.
 */
public interface ClassFilter {

    boolean matches(Class<?> clazz);
}
