package com.xujn.minispring.aop;

/**
 * Aggregates the target, pointcut, and interceptor used for proxy creation.
 * Constraint: Phase 2 models a single interceptor per advisor configuration.
 * Thread-safety: mutable during setup, read-only during runtime.
 */
public class AdvisedSupport {

    private TargetSource targetSource;
    private Pointcut pointcut;
    private MethodInterceptor methodInterceptor;

    public TargetSource getTargetSource() {
        return targetSource;
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public MethodInterceptor getMethodInterceptor() {
        return methodInterceptor;
    }

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }
}
