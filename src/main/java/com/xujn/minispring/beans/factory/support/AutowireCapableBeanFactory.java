package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.core.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;

/**
 * Bean factory capable of reflective instantiation and field-based dependency injection.
 * Constraint: Phase 3 adds third-level-cache exposure and constructor-cycle fast-fail on top of Phase 2 lifecycle.
 * Thread-safety: designed for single-threaded bootstrap.
 */
public abstract class AutowireCapableBeanFactory extends AbstractBeanFactory {

    private final ThreadLocal<Deque<String>> constructorResolutionPath =
            ThreadLocal.withInitial(ArrayDeque::new);

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) {
        Object bean = createBeanInstance(beanName, beanDefinition);
        if (beanDefinition.isSingleton() && isSingletonCurrentlyInCreation(beanName)) {
            addSingletonFactory(beanName, () -> getEarlyBeanReference(bean, beanName));
        }
        populateBean(beanName, bean, beanDefinition);
        Object exposedObject = initializeBean(beanName, bean);
        if (beanDefinition.isSingleton() && containsEarlySingleton(beanName)) {
            exposedObject = getSingleton(beanName, false);
        }
        registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);
        return exposedObject;
    }

    protected Object createBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Constructor<?>[] constructors = beanDefinition.getBeanClass().getDeclaredConstructors();
        Constructor<?> noArgs = Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameterCount() == 0)
                .findFirst()
                .orElse(null);
        if (noArgs != null) {
            return ReflectionUtils.instantiateClass(beanDefinition.getBeanClass(), beanName);
        }
        Constructor<?> constructor = Arrays.stream(constructors)
                .max(Comparator.comparingInt(Constructor::getParameterCount))
                .orElseThrow();
        constructorResolutionPath.get().addLast(beanName);
        try {
            Object[] args = Arrays.stream(constructor.getParameterTypes())
                    .map(this::getBean)
                    .toArray();
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (ReflectiveOperationException ex) {
            throw new com.xujn.minispring.exception.BeansException(
                    "Failed to instantiate bean '" + beanName + "' via constructor on type [" +
                            beanDefinition.getBeanClass().getName() + "]", ex);
        } finally {
            Deque<String> path = constructorResolutionPath.get();
            path.removeLastOccurrence(beanName);
            if (path.isEmpty()) {
                constructorResolutionPath.remove();
            }
        }
    }

    protected void populateBean(String beanName, Object bean, BeanDefinition beanDefinition) {
        for (Field field : ReflectionUtils.getDeclaredFields(beanDefinition.getBeanClass())) {
            if (!field.isAnnotationPresent(Autowired.class)) {
                continue;
            }
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }
            Object dependency = getBean(field.getType());
            ReflectionUtils.setField(field, bean, dependency, beanName);
        }
    }

    protected Object initializeBean(String beanName, Object bean) {
        Object wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);
        invokeInitMethods(beanName, wrappedBean);
        return applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
    }

    protected Object applyBeanPostProcessorsBeforeInitialization(Object bean, String beanName) {
        Object current = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object candidate = beanPostProcessor.postProcessBeforeInitialization(current, beanName);
            if (candidate != null) {
                current = candidate;
            }
        }
        return current;
    }

    protected Object applyBeanPostProcessorsAfterInitialization(Object bean, String beanName) {
        Object current = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            Object candidate = beanPostProcessor.postProcessAfterInitialization(current, beanName);
            if (candidate != null) {
                current = candidate;
            }
        }
        return current;
    }

    protected void invokeInitMethods(String beanName, Object bean) {
        if (bean instanceof InitializingBean initializingBean) {
            initializingBean.afterPropertiesSet();
        }
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton() && bean instanceof DisposableBean disposableBean) {
            registerDisposableBean(beanName, disposableBean);
        }
    }

    protected Object getEarlyBeanReference(Object bean, String beanName) {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof SmartInstantiationAwareBeanPostProcessor smartProcessor) {
                exposedObject = smartProcessor.getEarlyBeanReference(exposedObject, beanName);
            }
        }
        return exposedObject;
    }

    protected abstract java.util.List<BeanPostProcessor> getBeanPostProcessors();

    @Override
    protected boolean isConstructorResolutionInProgress() {
        return !constructorResolutionPath.get().isEmpty();
    }
}
