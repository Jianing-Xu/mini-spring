package com.xujn.minispring.test.phase2.aop.interceptor;

import com.xujn.minispring.aop.MethodInterceptor;
import com.xujn.minispring.aop.MethodInvocation;

public class LoggingInterceptor implements MethodInterceptor {

    private static boolean intercepted;

    public static void reset() {
        intercepted = false;
    }

    public static boolean isIntercepted() {
        return intercepted;
    }

    @Override
    public Object invoke(MethodInvocation invocation) {
        intercepted = true;
        return invocation.proceed();
    }
}
