package com.xujn.minispring.tx.interceptor;

import java.lang.reflect.Method;

/**
 * Strategy for resolving transaction metadata from method and target-class context.
 * Constraint: method-level metadata takes precedence over class-level metadata.
 * Thread-safety: implementations should be stateless or cache safely.
 */
public interface TransactionAttributeSource {

    TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass);

    boolean hasTransactionAttribute(Class<?> targetClass);
}
