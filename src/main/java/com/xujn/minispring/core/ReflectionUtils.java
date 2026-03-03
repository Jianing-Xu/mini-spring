package com.xujn.minispring.core;

import com.xujn.minispring.exception.BeansException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Reflection helper methods used by the container.
 * Constraint: Phase 1 instantiates beans only through a no-arg constructor.
 * Thread-safety: stateless utility methods are thread-safe.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Object instantiateClass(Class<?> beanClass, String beanName) {
        try {
            Constructor<?> constructor = beanClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException ex) {
            throw new BeansException("Bean '" + beanName + "' of type [" + beanClass.getName() +
                    "] requires a no-arg constructor in Phase 1", ex);
        } catch (ReflectiveOperationException ex) {
            throw new BeansException("Failed to instantiate bean '" + beanName + "' of type [" +
                    beanClass.getName() + "]", ex);
        }
    }

    public static Field[] getDeclaredFields(Class<?> beanClass) {
        return beanClass.getDeclaredFields();
    }

    public static void setField(Field field, Object target, Object value, String beanName) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException ex) {
            throw new BeansException("Failed to inject field '" + field.getName() + "' of type [" +
                    field.getType().getName() + "] on bean '" + beanName + "'", ex);
        }
    }
}
