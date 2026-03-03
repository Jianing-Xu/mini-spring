package com.xujn.minispring.examples.phase3.fixture;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.aspectj.AspectJExpressionPointcut;
import com.xujn.minispring.aop.framework.autoproxy.AutoProxyCreator;
import com.xujn.minispring.context.annotation.Component;

@Component
public class Phase3AutoProxyCreator extends AutoProxyCreator {

    public Phase3AutoProxyCreator() {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setPointcut(
                new AspectJExpressionPointcut("execution(* com.xujn.minispring.examples.phase3.fixture.AaaProxiedBImpl.*(..))"));
        advisedSupport.setMethodInterceptor(invocation -> {
            Phase3State.intercepted = true;
            return invocation.proceed();
        });
        addAdvisor(advisedSupport);
    }
}
