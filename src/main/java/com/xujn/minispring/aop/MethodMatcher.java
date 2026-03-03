package com.xujn.minispring.aop;

import java.lang.reflect.Method;

/**
 * Matches whether a specific method on a target class should be intercepted.
 * Constraint: Phase 2 supports method name matching with wildcard support.
 * Thread-safety: implementations should be immutable.
 */
public interface MethodMatcher {

    boolean matches(Method method, Class<?> targetClass);
}
