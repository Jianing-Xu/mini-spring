package com.xujn.minispring.aop.framework;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * JDK dynamic proxy implementation for interface-based beans.
 * Constraint: only methods matched by the advisor's pointcut run through interceptors.
 * Thread-safety: stateless aside from immutable advised configuration.
 */
public class JdkDynamicAopProxy implements InvocationHandler {

    private final AdvisedSupport advisedSupport;

    public JdkDynamicAopProxy(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(
                advisedSupport.getTargetSource().getTarget().getClass().getClassLoader(),
                advisedSupport.getTargetSource().getTargetInterfaces(),
                this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target = advisedSupport.getTargetSource().getTarget();
        if (method.getDeclaringClass() == Object.class
                || !advisedSupport.getPointcut().getMethodMatcher().matches(method, target.getClass())) {
            try {
                method.setAccessible(true);
                return method.invoke(target, args);
            } catch (java.lang.reflect.InvocationTargetException ex) {
                throw new RuntimeException(ex.getTargetException());
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
        MethodInterceptor interceptor = advisedSupport.getMethodInterceptor();
        ReflectiveMethodInvocation invocation =
                new ReflectiveMethodInvocation(target, method, args, List.of(interceptor));
        return invocation.proceed();
    }
}
