package com.xujn.minispring.aop.framework.autoproxy;

import com.xujn.minispring.aop.AdvisedSupport;
import com.xujn.minispring.aop.TargetSource;
import com.xujn.minispring.aop.framework.ProxyFactory;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanPostProcessor that wraps matched beans in JDK dynamic proxies.
 * Constraint: Phase 3 exposes proxies early for circular dependencies and avoids duplicate proxy creation.
 * Thread-safety: advisor list is configured during bootstrap and then read-only.
 */
public class AutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor {

    private final List<AdvisedSupport> advisors = new ArrayList<>();
    private final Set<String> earlyProxyReferences =
            Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<String, Integer> earlyReferenceCounts = new ConcurrentHashMap<>();

    public void addAdvisor(AdvisedSupport advisedSupport) {
        advisors.add(advisedSupport);
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        earlyProxyReferences.add(beanName);
        earlyReferenceCounts.merge(beanName, 1, Integer::sum);
        return wrapIfNecessary(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof BeanPostProcessor || advisors.isEmpty()) {
            return bean;
        }
        if (earlyProxyReferences.remove(beanName)) {
            return bean;
        }
        return wrapIfNecessary(bean, beanName);
    }

    public int getEarlyReferenceCount(String beanName) {
        return earlyReferenceCounts.getOrDefault(beanName, 0);
    }

    private Object wrapIfNecessary(Object bean, String beanName) {
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
            runtimeAdvisor.setTargetSource(new TargetSource(bean, interfaces));
            return new ProxyFactory(runtimeAdvisor).getProxy();
        }
        return bean;
    }
}
