package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.config.SingletonBeanRegistry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default singleton registry backed by concurrent collections.
 * Constraint: Phase 1 only stores fully initialized singleton instances.
 * Thread-safety: singleton caches are thread-safe, but full refresh lifecycle is assumed single-threaded.
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
    }

    @Override
    public boolean containsSingleton(String beanName) {
        return singletonObjects.containsKey(beanName);
    }

    protected void beforeSingletonCreation(String beanName) {
        singletonsCurrentlyInCreation.add(beanName);
    }

    protected void afterSingletonCreation(String beanName) {
        singletonsCurrentlyInCreation.remove(beanName);
    }

    protected boolean isSingletonCurrentlyInCreation(String beanName) {
        return singletonsCurrentlyInCreation.contains(beanName);
    }

    public void registerDisposableBean(String beanName, DisposableBean disposableBean) {
        disposableBeans.put(beanName, disposableBean);
    }

    public void destroySingletons() {
        for (Map.Entry<String, DisposableBean> entry : new LinkedHashMap<>(disposableBeans).entrySet()) {
            try {
                entry.getValue().destroy();
            } catch (RuntimeException ex) {
                System.err.println("WARN destroy bean '" + entry.getKey() + "' failed: " + ex.getMessage());
            }
        }
        disposableBeans.clear();
        singletonObjects.clear();
    }
}
