package com.xujn.minispring.tx.interceptor;

import com.xujn.minispring.aop.MethodInterceptor;
import com.xujn.minispring.aop.MethodInvocation;
import com.xujn.minispring.tx.transaction.PlatformTransactionManager;
import com.xujn.minispring.tx.transaction.TransactionStatus;

/**
 * Around interceptor that opens, commits, or rolls back local transactions.
 * Constraint: only transactional methods resolved by the attribute source are intercepted.
 * Thread-safety: stateless aside from immutable collaborators.
 */
public class TransactionInterceptor implements MethodInterceptor {

    private final PlatformTransactionManager transactionManager;
    private final TransactionAttributeSource transactionAttributeSource;

    public TransactionInterceptor(PlatformTransactionManager transactionManager,
                                  TransactionAttributeSource transactionAttributeSource) {
        this.transactionManager = transactionManager;
        this.transactionAttributeSource = transactionAttributeSource;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = invocation.getThis().getClass();
        TransactionAttribute transactionAttribute =
                transactionAttributeSource.getTransactionAttribute(invocation.getMethod(), targetClass);
        if (transactionAttribute == null) {
            return invocation.proceed();
        }
        TransactionStatus status = transactionManager.getTransaction(transactionAttribute);
        try {
            Object result = invocation.proceed();
            transactionManager.commit(status);
            return result;
        } catch (Throwable ex) {
            if (transactionAttribute.rollbackOn(ex)) {
                transactionManager.rollback(status);
            } else {
                transactionManager.commit(status);
            }
            throw ex;
        }
    }
}
