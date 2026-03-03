package com.xujn.minispring.aop.framework;

import com.xujn.minispring.aop.MethodInterceptor;
import com.xujn.minispring.aop.MethodInvocation;
import com.xujn.minispring.exception.BeansException;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Reflection-based MethodInvocation implementation with a simple interceptor chain.
 * Constraint: Phase 2 interceptor chain ordering follows the provided list order.
 * Thread-safety: single-use invocation instance, not thread-safe.
 */
public class ReflectiveMethodInvocation implements MethodInvocation {

    private final Object target;
    private final Method method;
    private final Object[] arguments;
    private final List<MethodInterceptor> interceptors;
    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object target, Method method, Object[] arguments,
                                      List<MethodInterceptor> interceptors) {
        this.target = target;
        this.method = method;
        this.arguments = arguments == null ? new Object[0] : arguments;
        this.interceptors = interceptors;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Object[] getArguments() {
        return arguments.clone();
    }

    @Override
    public Object getThis() {
        return target;
    }

    @Override
    public Object proceed() {
        if (currentInterceptorIndex == interceptors.size() - 1) {
            try {
                method.setAccessible(true);
                return method.invoke(target, arguments);
            } catch (ReflectiveOperationException ex) {
                throw new BeansException("Failed to invoke method '" + method.getName() +
                        "' on target [" + target.getClass().getName() + "]", ex);
            }
        }
        currentInterceptorIndex++;
        return interceptors.get(currentInterceptorIndex).invoke(this);
    }
}
