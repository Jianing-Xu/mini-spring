package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.beans.factory.InitializingBean;
import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.core.ReflectionUtils;
import com.xujn.minispring.exception.BeansException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        if (beanDefinition.isFactoryMethod()) {
            return createBeanByFactoryMethod(beanName, beanDefinition);
        }
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

    private Object createBeanByFactoryMethod(String beanName, BeanDefinition beanDefinition) {
        Object factoryBean = getBean(beanDefinition.getFactoryBeanName());
        Method factoryMethod;
        try {
            factoryMethod = factoryBean.getClass().getDeclaredMethod(
                    beanDefinition.getFactoryMethodName(),
                    beanDefinition.getFactoryMethodParameterTypes()
            );
        } catch (NoSuchMethodException ex) {
            throw new BeansException("Failed to locate @Bean factory method '" +
                    beanDefinition.getFactoryMethodName() + "' on bean '" +
                    beanDefinition.getFactoryBeanName() + "' for bean '" + beanName + "'", ex);
        }
        try {
            factoryMethod.setAccessible(true);
            Object[] args = resolveFactoryMethodArguments(beanName, beanDefinition, factoryMethod);
            Object instance = factoryMethod.invoke(factoryBean, args);
            if (instance == null) {
                throw new BeansException("@Bean method returned null: beanName='" + beanName +
                        "', factoryBeanName='" + beanDefinition.getFactoryBeanName() +
                        "', factoryMethodName='" + beanDefinition.getFactoryMethodName() + "'");
            }
            return instance;
        } catch (IllegalAccessException ex) {
            throw new BeansException("Failed to access @Bean factory method '" +
                    beanDefinition.getFactoryMethodName() + "' for bean '" + beanName + "'", ex);
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            throw new BeansException("Failed to invoke @Bean factory method '" +
                    beanDefinition.getFactoryMethodName() + "' for bean '" + beanName +
                    "': " + targetException.getMessage(), targetException);
        }
    }

    private Object[] resolveFactoryMethodArguments(String beanName, BeanDefinition beanDefinition, Method factoryMethod) {
        Class<?>[] parameterTypes = beanDefinition.getFactoryMethodParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            try {
                args[i] = getBean(parameterType);
            } catch (BeansException ex) {
                throw new BeansException("Failed to resolve parameter index " + i + " of type [" +
                        parameterType.getName() + "] for @Bean factory method '" +
                        factoryMethod.getName() + "' on bean '" + beanDefinition.getFactoryBeanName() + "'", ex);
            }
        }
        return args;
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
        invokeCustomInitMethod(beanName, bean, getBeanDefinition(beanName));
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        if (!beanDefinition.isSingleton()) {
            return;
        }
        boolean hasDestroyInterface = bean instanceof DisposableBean;
        boolean hasCustomDestroyMethod = beanDefinition.getDestroyMethodName() != null
                && !beanDefinition.getDestroyMethodName().isBlank();
        if (hasDestroyInterface || hasCustomDestroyMethod) {
            registerDisposableBean(beanName,
                    new DisposableBeanAdapter(beanName, bean, beanDefinition.getDestroyMethodName()));
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

    private void invokeCustomInitMethod(String beanName, Object bean, BeanDefinition beanDefinition) {
        String initMethodName = beanDefinition.getInitMethodName();
        if (initMethodName == null || initMethodName.isBlank()) {
            return;
        }
        if (bean instanceof InitializingBean && "afterPropertiesSet".equals(initMethodName)) {
            return;
        }
        try {
            Method initMethod = bean.getClass().getMethod(initMethodName);
            initMethod.setAccessible(true);
            initMethod.invoke(bean);
        } catch (NoSuchMethodException ex) {
            throw new BeansException("Failed to find init method '" + initMethodName +
                    "' on bean '" + beanName + "'", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new BeansException("Failed to invoke init method '" + initMethodName +
                    "' on bean '" + beanName + "'", ex);
        }
    }

    @Override
    protected boolean isConstructorResolutionInProgress() {
        return !constructorResolutionPath.get().isEmpty();
    }
}
