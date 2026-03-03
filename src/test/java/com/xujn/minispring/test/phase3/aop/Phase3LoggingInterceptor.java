package com.xujn.minispring.test.phase3.aop;

import com.xujn.minispring.aop.MethodInterceptor;
import com.xujn.minispring.aop.MethodInvocation;

public class Phase3LoggingInterceptor implements MethodInterceptor {

    private static boolean intercepted;

    public static void reset() {
        intercepted = false;
    }

    public static boolean isIntercepted() {
        return intercepted;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        intercepted = true;
        return invocation.proceed();
    }
}
