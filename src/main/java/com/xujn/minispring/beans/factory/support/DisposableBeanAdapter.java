package com.xujn.minispring.beans.factory.support;

import com.xujn.minispring.beans.factory.DisposableBean;
import com.xujn.minispring.exception.BeansException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Bridges destroy callbacks declared through interfaces and JavaConfig destroyMethod metadata.
 * Constraint: custom destroy methods are invoked once and skipped when they would duplicate DisposableBean.destroy().
 * Thread-safety: adapter instances are bound to a single bean and used during context shutdown.
 */
public class DisposableBeanAdapter implements DisposableBean {

    private final String beanName;
    private final Object bean;
    private final String destroyMethodName;

    public DisposableBeanAdapter(String beanName, Object bean, String destroyMethodName) {
        this.beanName = beanName;
        this.bean = bean;
        this.destroyMethodName = destroyMethodName == null ? "" : destroyMethodName.trim();
    }

    @Override
    public void destroy() {
        if (bean instanceof DisposableBean disposableBean) {
            disposableBean.destroy();
        }
        if (!destroyMethodName.isEmpty()
                && !(bean instanceof DisposableBean && "destroy".equals(destroyMethodName))) {
            invokeCustomDestroyMethod();
        }
    }

    private void invokeCustomDestroyMethod() {
        try {
            Method destroyMethod = bean.getClass().getMethod(destroyMethodName);
            destroyMethod.setAccessible(true);
            destroyMethod.invoke(bean);
        } catch (NoSuchMethodException ex) {
            throw new BeansException("Failed to find destroy method '" + destroyMethodName +
                    "' on bean '" + beanName + "'", ex);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new BeansException("Failed to invoke destroy method '" + destroyMethodName +
                    "' on bean '" + beanName + "'", ex);
        }
    }
}
