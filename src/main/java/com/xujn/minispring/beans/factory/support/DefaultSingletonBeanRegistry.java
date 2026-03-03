package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.ObjectFactory;
import com.xujn.minispring.beans.factory.config.SingletonBeanRegistry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default singleton registry backed by concurrent collections.
 * Constraint: Phase 3 maintains three singleton caches for circular dependency resolution.
 * Thread-safety: singleton caches are thread-safe, but full refresh lifecycle is assumed single-threaded.
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    private final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> earlySingletonObjects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>();
    private final Set<String> singletonsCurrentlyInCreation =
            Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<String, DisposableBean> disposableBeans = new LinkedHashMap<>();

    @Override
    public Object getSingleton(String beanName) {
        return getSingleton(beanName, true);
    }

    protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject != null) {
            return singletonObject;
        }
        if (!isSingletonCurrentlyInCreation(beanName)) {
            return null;
        }
        Object earlySingletonObject = earlySingletonObjects.get(beanName);
        if (earlySingletonObject != null) {
            return earlySingletonObject;
        }
        if (!allowEarlyReference) {
            return null;
        }
        ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
        if (singletonFactory == null) {
            return null;
        }
        Object earlyReference = singletonFactory.getObject();
        earlySingletonObjects.put(beanName, earlyReference);
        singletonFactories.remove(beanName);
        return earlyReference;
    }

    @Override
    public void registerSingleton(String beanName, Object singletonObject) {
        singletonObjects.put(beanName, singletonObject);
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
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

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
        if (!containsSingleton(beanName)) {
            singletonFactories.put(beanName, singletonFactory);
            earlySingletonObjects.remove(beanName);
        }
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
        earlySingletonObjects.clear();
        singletonFactories.clear();
    }

    public boolean containsEarlySingleton(String beanName) {
        return earlySingletonObjects.containsKey(beanName);
    }

    public boolean containsSingletonFactory(String beanName) {
        return singletonFactories.containsKey(beanName);
    }

    public Map<String, Object> getSingletonObjectsSnapshot() {
        return new HashMap<>(singletonObjects);
    }

    public Map<String, Object> getEarlySingletonObjectsSnapshot() {
        return new HashMap<>(earlySingletonObjects);
    }

    public Map<String, ObjectFactory<?>> getSingletonFactoriesSnapshot() {
        return new HashMap<>(singletonFactories);
    }
}
