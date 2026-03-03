package com.xujn.minispring.tx.support;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.ClassFilter;
import com.xujn.minispring.aop.MethodMatcher;
import com.xujn.minispring.aop.Pointcut;
import com.xujn.minispring.aop.TargetSource;
import com.xujn.minispring.aop.framework.ProxyFactory;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import com.xujn.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.xujn.minispring.tx.interceptor.AnnotationTransactionAttributeSource;
import com.xujn.minispring.tx.interceptor.TransactionAttributeSource;
import com.xujn.minispring.tx.interceptor.TransactionInterceptor;
import com.xujn.minispring.tx.transaction.PlatformTransactionManager;
import com.xujn.minispring.tx.transaction.TransactionResourceFactory;

import java.lang.reflect.Method;

/**
 * Internal BeanPostProcessor that wraps transactional beans with a JDK proxy.
 * Constraint: only interface-based beans are proxied; infrastructure beans are skipped.
 * Thread-safety: immutable collaborators plus container-managed lazy lookups.
 */
public class TransactionAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor {

    private final DefaultListableBeanFactory beanFactory;
    private final TransactionAttributeSource transactionAttributeSource = new AnnotationTransactionAttributeSource();

    public TransactionAutoProxyCreator(DefaultListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (isInfrastructureBean(bean) || !transactionAttributeSource.hasTransactionAttribute(bean.getClass())) {
            return bean;
        }
        Class<?>[] interfaces = bean.getClass().getInterfaces();
        if (interfaces.length == 0) {
            System.err.println("WARN bean '" + beanName +
                    "' declares @Transactional but has no interfaces; skip transaction proxy");
            return bean;
        }
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setPointcut(new TransactionPointcut(transactionAttributeSource));
        advisedSupport.setMethodInterceptor(
                new TransactionInterceptor(beanFactory.getBean(PlatformTransactionManager.class), transactionAttributeSource)
        );
        advisedSupport.setTargetSource(new TargetSource(bean, interfaces));
        return new ProxyFactory(advisedSupport).getProxy();
    }

    private boolean isInfrastructureBean(Object bean) {
        return bean instanceof BeanPostProcessor
                || bean instanceof PlatformTransactionManager
                || bean instanceof TransactionResourceFactory
                || bean instanceof TransactionInterceptor
                || bean instanceof TransactionAttributeSource;
    }

    private static class TransactionPointcut implements Pointcut {

        private final TransactionAttributeSource transactionAttributeSource;

        private TransactionPointcut(TransactionAttributeSource transactionAttributeSource) {
            this.transactionAttributeSource = transactionAttributeSource;
        }

        @Override
        public ClassFilter getClassFilter() {
            return transactionAttributeSource::hasTransactionAttribute;
        }

        @Override
        public MethodMatcher getMethodMatcher() {
            return new MethodMatcher() {
                @Override
                public boolean matches(Method method, Class<?> targetClass) {
                    return transactionAttributeSource.getTransactionAttribute(method, targetClass) != null;
                }
            };
        }
    }
}
