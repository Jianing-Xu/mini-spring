package com.xujn.minispring.tx.interceptor;

import com.xujn.minispring.tx.annotation.Transactional;

import java.lang.reflect.Method;

/**
 * Resolves transaction metadata from {@link Transactional} annotations.
 * Constraint: method-level annotations override class-level annotations.
 * Thread-safety: stateless implementation.
 */
public class AnnotationTransactionAttributeSource implements TransactionAttributeSource {

    @Override
    public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        Method specificMethod = findSpecificMethod(method, targetClass);
        if (specificMethod.isAnnotationPresent(Transactional.class)) {
            return new TransactionAttribute(buildDescriptor(specificMethod), "method");
        }
        if (targetClass.isAnnotationPresent(Transactional.class)) {
            return new TransactionAttribute(buildDescriptor(specificMethod), "class");
        }
        if (method.isAnnotationPresent(Transactional.class)) {
            return new TransactionAttribute(buildDescriptor(method), "method");
        }
        return null;
    }

    @Override
    public boolean hasTransactionAttribute(Class<?> targetClass) {
        if (targetClass.isAnnotationPresent(Transactional.class)) {
            return true;
        }
        for (Method method : targetClass.getMethods()) {
            if (method.isAnnotationPresent(Transactional.class)) {
                return true;
            }
        }
        for (Class<?> interfaceType : targetClass.getInterfaces()) {
            for (Method method : interfaceType.getMethods()) {
                if (method.isAnnotationPresent(Transactional.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Method findSpecificMethod(Method method, Class<?> targetClass) {
        try {
            return targetClass.getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException ex) {
            return method;
        }
    }

    private String buildDescriptor(Method method) {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }
}
