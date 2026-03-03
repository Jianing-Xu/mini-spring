package com.xujn.minispring.test.phase2.aop.logging;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.aspectj.AspectJExpressionPointcut;
import com.xujn.minispring.aop.framework.autoproxy.AutoProxyCreator;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.test.phase2.aop.interceptor.LoggingInterceptor;

@Component
public class AopAutoProxyCreator extends AutoProxyCreator {

    public AopAutoProxyCreator() {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setPointcut(
                new AspectJExpressionPointcut("execution(* com.xujn.minispring.test.phase2.aop.service.*.*(..))"));
        advisedSupport.setMethodInterceptor(new LoggingInterceptor());
        addAdvisor(advisedSupport);
    }
}
