package com.xujn.minispring.aop;

/**
 * Around advice contract used by the JDK proxy implementation.
 * Constraint: interceptors should call proceed() when they want the target invocation to continue.
 * Thread-safety: depends on the implementing interceptor.
 */
public interface MethodInterceptor {

    Object invoke(MethodInvocation invocation) throws Throwable;
}
