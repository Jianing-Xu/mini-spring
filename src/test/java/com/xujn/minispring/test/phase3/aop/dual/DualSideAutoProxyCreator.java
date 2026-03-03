package com.xujn.minispring.test.phase3.aop.dual;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.aspectj.AspectJExpressionPointcut;
import com.xujn.minispring.aop.framework.autoproxy.AutoProxyCreator;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.test.phase3.aop.Phase3LoggingInterceptor;

@Component
public class DualSideAutoProxyCreator extends AutoProxyCreator {

    public DualSideAutoProxyCreator() {
        AdvisedSupport advisedSupport = new AdvisedSupport();
        advisedSupport.setPointcut(new AspectJExpressionPointcut(
                "execution(* com.xujn.minispring.test.phase3.aop.dual.*.*(..))"));
        advisedSupport.setMethodInterceptor(new Phase3LoggingInterceptor());
        addAdvisor(advisedSupport);
    }
}
