package com.xujn.minispring.core;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.context.annotation.ComponentScan;
import com.xujn.minispring.context.annotation.Scope;
import com.xujn.minispring.exception.BeansException;

/**
 * Utility methods for reading and normalizing annotation metadata.
 * Constraint: only Phase 1 and Phase 2 annotation metadata are supported here.
 * Thread-safety: stateless utility methods are thread-safe.
 */
public final class AnnotationUtils {

    private AnnotationUtils() {
    }

    public static boolean isComponent(Class<?> beanClass) {
        return beanClass.isAnnotationPresent(Component.class);
    }

    public static String resolveBeanName(Class<?> beanClass) {
        Component component = beanClass.getAnnotation(Component.class);
        if (component != null && !component.value().isBlank()) {
            return component.value().trim();
        }
        String simpleName = beanClass.getSimpleName();
        if (simpleName.isEmpty()) {
            throw new BeansException("Cannot derive bean name from class " + beanClass.getName());
        }
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    public static String resolveScope(Class<?> beanClass) {
        Scope scope = beanClass.getAnnotation(Scope.class);
        if (scope == null || scope.value().isBlank()) {
            return BeanDefinition.SCOPE_SINGLETON;
        }
        String scopeValue = scope.value().trim();
        if (!BeanDefinition.SCOPE_SINGLETON.equals(scopeValue)
                && !BeanDefinition.SCOPE_PROTOTYPE.equals(scopeValue)) {
            throw new BeansException("Bean class [" + beanClass.getName() +
                    "] declares unsupported scope '" + scopeValue + "'");
        }
        return scopeValue;
    }

    public static String[] resolveBasePackages(Class<?> configClass) {
        ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
        if (componentScan == null) {
            throw new BeansException("Config class [" + configClass.getName() +
                    "] must declare @ComponentScan");
        }
        if (componentScan.value().length > 0) {
            return componentScan.value();
        }
        Package configPackage = configClass.getPackage();
        return new String[]{configPackage == null ? "" : configPackage.getName()};
    }
}
