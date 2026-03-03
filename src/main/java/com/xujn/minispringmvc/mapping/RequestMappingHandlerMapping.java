package com.xujn.minispringmvc.mapping;

import com.xujn.minispring.beans.factory.config.BeanDefinition;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.annotation.Controller;
import com.xujn.minispringmvc.annotation.RequestMapping;
import com.xujn.minispringmvc.context.MvcApplicationContext;
import com.xujn.minispringmvc.context.support.DefaultMvcInfrastructureInitializer;
import com.xujn.minispringmvc.interceptor.HandlerInterceptor;
import com.xujn.minispringmvc.servlet.HandlerExecutionChain;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.support.Ordered;

import java.lang.reflect.Method;

/**
 * Annotation-driven handler mapping for @Controller and @RequestMapping.
 * Constraint: Phase 1 supports exact method/path lookups and detects mapping conflicts during init.
 * Thread-safety: initialized once and then read-only.
 */
@Component
public class RequestMappingHandlerMapping implements HandlerMapping, Ordered {

    private final DefaultMvcInfrastructureInitializer infrastructureInitializer = new DefaultMvcInfrastructureInitializer();
    private final RequestMappingRegistry registry = new RequestMappingRegistry();
    private java.util.List<HandlerInterceptor> interceptors = java.util.List.of();
    private boolean initialized;

    public void initialize(MvcApplicationContext context) {
        if (initialized) {
            return;
        }
        this.interceptors = infrastructureInitializer.initializeBeans(context, HandlerInterceptor.class);
        for (String beanName : context.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = context.getBeanDefinition(beanName);
            Class<?> beanClass = beanDefinition.getBeanClass();
            if (!beanClass.isAnnotationPresent(Controller.class)) {
                continue;
            }
            registerController(beanName, beanClass, context.getBean(beanName));
        }
        initialized = true;
    }

    private void registerController(String beanName, Class<?> beanClass, Object bean) {
        String classLevelPath = "";
        RequestMapping classMapping = beanClass.getAnnotation(RequestMapping.class);
        if (classMapping != null) {
            classLevelPath = classMapping.path();
        }
        for (Method method : beanClass.getDeclaredMethods()) {
            RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
            if (methodMapping == null) {
                continue;
            }
            String path = combinePaths(classLevelPath, methodMapping.path());
            RequestMappingInfo mappingInfo = new RequestMappingInfo(methodMapping.method(), path);
            registry.register(mappingInfo, new HandlerMethod(beanName, bean, beanClass, method));
        }
    }

    @Override
    public HandlerExecutionChain getHandler(WebRequest request) {
        HandlerMethod handlerMethod = registry.getHandlerMethod(request.getMethod(), request.getRequestUri());
        return handlerMethod == null ? null : new HandlerExecutionChain(handlerMethod, interceptors);
    }

    public int getRegistrySize() {
        return registry.size();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String combinePaths(String classLevelPath, String methodLevelPath) {
        String classPath = RequestMappingInfo.normalizePath(classLevelPath == null || classLevelPath.isBlank() ? "/" : classLevelPath);
        String methodPath = RequestMappingInfo.normalizePath(methodLevelPath == null || methodLevelPath.isBlank() ? "/" : methodLevelPath);
        if ("/".equals(classPath)) {
            return methodPath;
        }
        if ("/".equals(methodPath)) {
            return classPath;
        }
        return RequestMappingInfo.normalizePath(classPath + "/" + methodPath.substring(1));
    }
}
