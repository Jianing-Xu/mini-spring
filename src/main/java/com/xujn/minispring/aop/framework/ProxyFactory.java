package com.xujn.minispring.aop.framework;

import com.xujn.minispring.aop.AdvisedSupport;

/**
 * Factory that creates proxies from advised configuration.
 * Constraint: Phase 2 only creates JDK dynamic proxies.
 * Thread-safety: immutable after advised configuration is set.
 */
public class ProxyFactory {

    private final AdvisedSupport advisedSupport;

    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    public Object getProxy() {
        return new JdkDynamicAopProxy(advisedSupport).getProxy();
    }
}
