package com.xujn.minispring.aop;

/**
 * Combines class and method matching rules for AOP interception.
 * Constraint: Phase 2 exposes only execution() expression semantics.
 * Thread-safety: implementations should be immutable.
 */
public interface Pointcut {

    ClassFilter getClassFilter();

    MethodMatcher getMethodMatcher();
}
