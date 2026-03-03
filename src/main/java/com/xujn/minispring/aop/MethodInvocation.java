package com.xujn.minispring.aop;

import java.lang.reflect.Method;

/**
 * Encapsulates a target method invocation exposed to interceptors.
 * Constraint: proceed() must eventually invoke the target method exactly once in Phase 2.
 * Thread-safety: invocation instances are single-use and not thread-safe.
 */
public interface MethodInvocation {

    Method getMethod();

    Object[] getArguments();

    Object getThis();

    Object proceed();
}
