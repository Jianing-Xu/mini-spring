package com.xujn.minispring.aop.framework.autoproxy;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.framework.ProxyFactory;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * BeanPostProcessor that wraps matched beans in JDK dynamic proxies.
 * Constraint: Phase 2 skips beans without interfaces and does not attempt early proxy exposure.
 * Thread-safety: advisor list is configured during bootstrap and then read-only.
 */
public class AutoProxyCreator implements BeanPostProcessor {

    private final List<AdvisedSupport> advisors = new ArrayList<>();

    public void addAdvisor(AdvisedSupport advisedSupport) {
        advisors.add(advisedSupport);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof BeanPostProcessor || advisors.isEmpty()) {
            return bean;
        }
        for (AdvisedSupport advisor : advisors) {
            if (!advisor.getPointcut().getClassFilter().matches(bean.getClass())) {
                continue;
            }
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            if (interfaces.length == 0) {
                System.err.println("WARN bean '" + beanName + "' matched pointcut but has no interfaces; skip proxy");
                return bean;
            }
            AdvisedSupport runtimeAdvisor = new AdvisedSupport();
            runtimeAdvisor.setPointcut(advisor.getPointcut());
            runtimeAdvisor.setMethodInterceptor(advisor.getMethodInterceptor());
            runtimeAdvisor.setTargetSource(new com.xujn.minispring.aop.TargetSource(bean, interfaces));
            return new ProxyFactory(runtimeAdvisor).getProxy();
        }
        return bean;
    }
}
