package com.xujn.minispring.test.phase2.aop.interceptor;

import com.xujn.minispring.aop.MethodInterceptor;
import com.xujn.minispring.aop.MethodInvocation;

public class ModifyingInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) {
        Object result = invocation.proceed();
        return result + "_modified";
    }
}
