package com.xujn.minispring.context.annotation;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.beans.factory.config.BeanDefinitionRegistry;
import com.xujn.minispring.core.AnnotationUtils;
import com.xujn.minispring.exception.BeansException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Recursive classpath scanner that turns @Component classes into BeanDefinitions.
 * Constraint: Phase 1 only supports file-based classpath scanning.
 * Thread-safety: intended for bootstrap-time single-threaded use.
 */
public class ClassPathBeanDefinitionScanner {

    private final BeanDefinitionRegistry registry;
    private final ClassLoader classLoader;

    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public void scan(String... basePackages) {
        if (basePackages == null) {
            return;
        }
        for (String basePackage : basePackages) {
            if (basePackage == null || basePackage.isBlank()) {
                continue;
            }
            scanPackage(basePackage.trim());
        }
    }

    private void scanPackage(String basePackage) {
        String packagePath = basePackage.replace('.', '/');
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (!"file".equals(resource.getProtocol())) {
                    continue;
                }
                File root = new File(decode(resource.getFile()));
                scanDirectory(basePackage, root);
            }
        } catch (Exception ex) {
            throw new BeansException("Failed to scan package '" + basePackage + "'", ex);
        }
    }

    private void scanDirectory(String currentPackage, File directory) throws ClassNotFoundException {
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        Arrays.sort(files, java.util.Comparator.comparing(File::getName));
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(currentPackage + "." + file.getName(), file);
                continue;
            }
            if (!file.getName().endsWith(".class") || file.getName().contains("$")) {
                continue;
            }
            String simpleClassName = file.getName().substring(0, file.getName().length() - 6);
            String className = currentPackage + "." + simpleClassName;
            Class<?> beanClass = Class.forName(className, false, classLoader);
            if (!isCandidateComponent(beanClass)) {
                continue;
            }
            BeanDefinition beanDefinition =
                    new BeanDefinition(beanClass, AnnotationUtils.resolveBeanName(beanClass));
            beanDefinition.setScope(AnnotationUtils.resolveScope(beanClass));
            registry.registerBeanDefinition(beanDefinition.getBeanName(), beanDefinition);
        }
    }

    private boolean isCandidateComponent(Class<?> beanClass) {
        int modifiers = beanClass.getModifiers();
        return AnnotationUtils.isComponent(beanClass)
                && !beanClass.isInterface()
                && !beanClass.isAnnotation()
                && !beanClass.isEnum()
                && !java.lang.reflect.Modifier.isAbstract(modifiers)
                && !beanClass.isMemberClass();
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            throw new BeansException("Failed to decode classpath resource path '" + value + "'", ex);
        }
    }
}
