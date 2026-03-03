package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.BeanFactory;
import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.exception.BeanCurrentlyInCreationException;
import com.xujn.minispring.exception.BeansException;
import com.xujn.minispring.exception.NoSuchBeanDefinitionException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Template bean factory that centralizes lookup, singleton caching, and circular dependency detection.
 * Constraint: Phase 2 supports singleton and prototype creation, and still fails fast on circular dependencies.
 * Thread-safety: singleton caches are concurrent, while creation path tracking is isolated per thread.
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    private final ThreadLocal<Deque<String>> currentCreationPath =
            ThreadLocal.withInitial(ArrayDeque::new);

    @Override
    public Object getBean(String name) {
        Objects.requireNonNull(name, "beanName must not be null");

        Object singleton = getSingleton(name, true);
        if (singleton != null) {
            if (!containsSingleton(name) && hasPrototypeInCreationPath()) {
                throw new BeanCurrentlyInCreationException(name, buildDependencyChain(name),
                        "prototype circular dependency is not supported");
            }
            return singleton;
        }

        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            throw new NoSuchBeanDefinitionException(name);
        }
        if (!beanDefinition.isSingleton() && !beanDefinition.isPrototype()) {
            throw new BeansException("Bean '" + name + "' declares unsupported scope '" +
                    beanDefinition.getScope() + "'");
        }
        if (isSingletonCurrentlyInCreation(name)) {
            if (beanDefinition.isPrototype()) {
                throw new BeanCurrentlyInCreationException(name, buildDependencyChain(name),
                        "prototype circular dependency is not supported");
            }
            if (isConstructorResolutionInProgress()) {
                throw new BeanCurrentlyInCreationException(name, buildDependencyChain(name),
                        "constructor injection circular dependency is not supported");
            }
            throw new BeanCurrentlyInCreationException(name, buildDependencyChain(name));
        }

        beforeSingletonCreation(name);
        Deque<String> creationPath = currentCreationPath.get();
        creationPath.addLast(name);
        try {
            Object bean = createBean(name, beanDefinition);
            if (beanDefinition.isSingleton()) {
                registerSingleton(name, bean);
            }
            return bean;
        } finally {
            // Cleanup must run even on failure; otherwise later lookups would see a fake cycle.
            creationPath.removeLastOccurrence(name);
            afterSingletonCreation(name);
            if (creationPath.isEmpty()) {
                currentCreationPath.remove();
            }
        }
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        Object bean = getBean(name);
        if (!requiredType.isInstance(bean)) {
            throw new BeansException("Bean named '" + name + "' is expected to be of type [" +
                    requiredType.getName() + "] but was actually [" + bean.getClass().getName() + "]");
        }
        return requiredType.cast(bean);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        List<String> candidateNames = new ArrayList<>();
        for (String beanName : getBeanDefinitionNamesForLookup()) {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            if (requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {
                candidateNames.add(beanName);
            }
        }

        if (candidateNames.isEmpty()) {
            throw new NoSuchBeanDefinitionException(requiredType);
        }
        if (candidateNames.size() > 1) {
            throw new BeansException("No unique bean of type [" + requiredType.getName() +
                    "], expected single bean but found " + candidateNames.size() + ": " +
                    String.join(", ", candidateNames));
        }
        return getBean(candidateNames.get(0), requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return containsSingleton(name) || containsBeanDefinition(name);
    }

    private String buildDependencyChain(String beanName) {
        Deque<String> creationPath = currentCreationPath.get();
        List<String> cycle = new ArrayList<>();
        boolean append = false;
        for (Iterator<String> iterator = creationPath.iterator(); iterator.hasNext(); ) {
            String current = iterator.next();
            if (current.equals(beanName)) {
                append = true;
            }
            if (append) {
                cycle.add(current);
            }
        }
        if (cycle.isEmpty()) {
            cycle.add(beanName);
        }
        cycle.add(beanName);
        return String.join(" -> ", cycle);
    }

    private boolean hasPrototypeInCreationPath() {
        for (String beanName : currentCreationPath.get()) {
            if (getBeanDefinition(beanName).isPrototype()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isConstructorResolutionInProgress() {
        return false;
    }

    protected abstract Object createBean(String beanName, BeanDefinition beanDefinition);

    protected abstract BeanDefinition getBeanDefinition(String beanName);

    protected abstract boolean containsBeanDefinition(String beanName);

    protected abstract String[] getBeanDefinitionNamesForLookup();
}
