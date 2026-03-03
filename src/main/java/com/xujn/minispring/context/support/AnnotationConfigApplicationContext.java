package com.xujn.minispring.context.support;

import com.xujn.minispring.beans.factory.config.BeanPostProcessor;
import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.context.ApplicationContext;
import com.xujn.minispring.context.annotation.ClassPathBeanDefinitionScanner;
import com.xujn.minispring.core.AnnotationUtils;
import com.xujn.minispring.beans.factory.support.DefaultListableBeanFactory;
import com.xujn.minispring.exception.BeansException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ApplicationContext implementation bootstrapped from annotation scanning.
 * Constraint: Phase 2 supports a single refresh and registers BeanPostProcessor beans before other singletons.
 * Thread-safety: refresh is not concurrent-safe and is expected to run once during startup.
 */
public class AnnotationConfigApplicationContext implements ApplicationContext {

    private final String[] basePackages;
    private final Class<?>[] componentScanConfigClasses;

    private DefaultListableBeanFactory beanFactory;
    private boolean refreshed;

    public AnnotationConfigApplicationContext() {
        this.basePackages = new String[0];
        this.componentScanConfigClasses = new Class<?>[0];
        refresh();
    }

    public AnnotationConfigApplicationContext(String... basePackages) {
        this.basePackages = basePackages == null ? new String[0] : Arrays.copyOf(basePackages, basePackages.length);
        this.componentScanConfigClasses = new Class<?>[0];
        refresh();
    }

    public AnnotationConfigApplicationContext(Class<?>... componentScanConfigClasses) {
        this.basePackages = new String[0];
        this.componentScanConfigClasses = componentScanConfigClasses == null
                ? new Class<?>[0]
                : Arrays.copyOf(componentScanConfigClasses, componentScanConfigClasses.length);
        refresh();
    }

    @Override
    public void refresh() {
        if (refreshed) {
            throw new BeansException("ApplicationContext has already been refreshed");
        }
        this.beanFactory = new DefaultListableBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        scanner.scan(resolveScanPackages());
        registerBeanPostProcessors(beanFactory);
        beanFactory.preInstantiateSingletons();
        this.refreshed = true;
    }

    @Override
    public void close() {
        if (beanFactory != null) {
            beanFactory.destroySingletons();
        }
    }

    @Override
    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return beanFactory.getBean(name, requiredType);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        return beanFactory.getBean(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    public BeanDefinition getBeanDefinition(String beanName) {
        return beanFactory.getBeanDefinition(beanName);
    }

    public boolean containsBeanDefinition(String beanName) {
        return beanFactory.containsBeanDefinition(beanName);
    }

    public String[] getBeanDefinitionNames() {
        return beanFactory.getBeanDefinitionNames();
    }

    public int getBeanDefinitionCount() {
        return beanFactory.getBeanDefinitionCount();
    }

    public DefaultListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    private void registerBeanPostProcessors(DefaultListableBeanFactory beanFactory) {
        for (String beanName : beanFactory.getBeanNamesForType(BeanPostProcessor.class)) {
            BeanPostProcessor beanPostProcessor = beanFactory.getBean(beanName, BeanPostProcessor.class);
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    private String[] resolveScanPackages() {
        List<String> scanPackages = new ArrayList<>();
        if (basePackages.length > 0) {
            scanPackages.addAll(Arrays.asList(basePackages));
        }
        for (Class<?> configClass : componentScanConfigClasses) {
            scanPackages.addAll(Arrays.asList(AnnotationUtils.resolveBasePackages(configClass)));
        }
        return scanPackages.toArray(String[]::new);
    }
}
