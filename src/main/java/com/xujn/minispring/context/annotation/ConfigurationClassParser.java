package com.xujn.minispring.context.annotation;

import com.xujn.minispring.beans.factory.config.BeanDefinition;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Parses {@link Configuration} bean definitions into factory-method metadata.
 * Constraint: only {@link Bean} methods declared on true configuration classes are considered.
 * Thread-safety: stateless parser intended for bootstrap-time use.
 */
public class ConfigurationClassParser {

    public Set<ConfigurationClass> parse(Set<BeanDefinition> configCandidates) {
        Set<ConfigurationClass> configurationClasses = new LinkedHashSet<>();
        for (BeanDefinition candidate : configCandidates) {
            if (!isConfigurationClass(candidate)) {
                continue;
            }
            ConfigurationClass configurationClass =
                    new ConfigurationClass(candidate.getBeanClass(), candidate.getBeanName());
            Method[] declaredMethods = candidate.getBeanClass().getDeclaredMethods();
            Arrays.sort(declaredMethods, Comparator.comparing(Method::getName));
            for (Method method : declaredMethods) {
                Bean bean = method.getAnnotation(Bean.class);
                if (bean == null || void.class.equals(method.getReturnType())) {
                    continue;
                }
                configurationClass.getBeanMethods().add(new BeanMethod(
                        method,
                        resolveBeanName(method, bean),
                        method.getReturnType(),
                        method.getParameterTypes(),
                        bean.initMethod().trim(),
                        bean.destroyMethod().trim()
                ));
            }
            configurationClasses.add(configurationClass);
        }
        return configurationClasses;
    }

    public boolean isConfigurationClass(BeanDefinition beanDefinition) {
        return beanDefinition.getBeanClass().isAnnotationPresent(Configuration.class);
    }

    private String resolveBeanName(Method method, Bean bean) {
        if (bean.name().length > 0 && !bean.name()[0].isBlank()) {
            return bean.name()[0].trim();
        }
        if (bean.value().length > 0 && !bean.value()[0].isBlank()) {
            return bean.value()[0].trim();
        }
        return method.getName();
    }
}
