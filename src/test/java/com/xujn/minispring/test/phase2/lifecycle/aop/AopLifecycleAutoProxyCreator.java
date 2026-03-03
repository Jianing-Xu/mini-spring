package com.xujn.minispring.test.phase2.lifecycle.aop;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.aspectj.AspectJExpressionPointcut;
import com.xujn.minispring.aop.framework.autoproxy.AutoProxyCreator;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.test.phase2.aop.interceptor.LoggingInterceptor;

@Component
public class AopLifecycleAutoProxyCreator extends AutoProxyCreator {

    public AopLifecycleAutoProxyCreator() {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setPointcut(new AspectJExpressionPointcut(
                "execution(* com.xujn.minispring.test.phase2.lifecycle.aop.*.*(..))"));
        advisedSupport.setMethodInterceptor(new LoggingInterceptor());
        addAdvisor(advisedSupport);
    }
}
