package com.xujn.minispring.examples.phase2.fixture;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.aspectj.AspectJExpressionPointcut;
import com.xujn.minispring.aop.framework.autoproxy.AutoProxyCreator;
import com.xujn.minispring.context.annotation.Component;

@Component
public class AppAutoProxyCreator extends AutoProxyCreator {

    public AppAutoProxyCreator() {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setPointcut(
                new AspectJExpressionPointcut("execution(* com.xujn.minispring.examples.phase2.fixture.*.*(..))"));
        advisedSupport.setMethodInterceptor(invocation -> {
            ExampleState.intercepted = true;
            return invocation.proceed();
        });
        addAdvisor(advisedSupport);
    }
}
