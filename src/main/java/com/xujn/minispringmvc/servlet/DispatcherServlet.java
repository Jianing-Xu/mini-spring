package com.xujn.minispringmvc.servlet;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.adapter.HandlerAdapter;
import com.xujn.minispringmvc.context.MvcApplicationContext;
import com.xujn.minispringmvc.context.support.DefaultMvcInfrastructureInitializer;
import com.xujn.minispringmvc.exception.DefaultHandlerExceptionResolver;
import com.xujn.minispringmvc.exception.ExceptionResolver;
import com.xujn.minispringmvc.exception.HandlerAdapterConflictException;
import com.xujn.minispringmvc.exception.MvcException;
import com.xujn.minispringmvc.exception.NoHandlerAdapterException;
import com.xujn.minispringmvc.exception.NoHandlerFoundException;
import com.xujn.minispringmvc.mapping.HandlerMapping;
import com.xujn.minispringmvc.mapping.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central MVC dispatcher that assembles infrastructure during init and dispatches requests afterward.
 * Constraint: Phase 1 handles mapping, adapter invocation, and default exception resolution only.
 * Thread-safety: init is expected to run once; dispatch reads immutable infrastructure lists afterward.
 */
public class DispatcherServlet {

    private final DefaultMvcInfrastructureInitializer infrastructureInitializer = new DefaultMvcInfrastructureInitializer();

    private MvcApplicationContext mvcApplicationContext;
    private List<HandlerMapping> handlerMappings = List.of();
    private List<HandlerAdapter> handlerAdapters = List.of();
    private List<ExceptionResolver> exceptionResolvers = List.of();
    private boolean initialized;

    public DispatcherServlet() {
    }

    public DispatcherServlet(AnnotationConfigApplicationContext applicationContext) {
        init(applicationContext);
    }

    public void init(AnnotationConfigApplicationContext applicationContext) {
        this.mvcApplicationContext = new MvcApplicationContext(applicationContext);
        this.handlerMappings = infrastructureInitializer.initializeBeans(mvcApplicationContext, HandlerMapping.class);
        this.handlerAdapters = infrastructureInitializer.initializeBeans(mvcApplicationContext, HandlerAdapter.class);
        this.exceptionResolvers = infrastructureInitializer.initializeBeans(mvcApplicationContext, ExceptionResolver.class);
        if (exceptionResolvers.isEmpty()) {
            this.exceptionResolvers = List.of(new DefaultHandlerExceptionResolver());
        }
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (handlerMapping instanceof RequestMappingHandlerMapping requestMappingHandlerMapping) {
                requestMappingHandlerMapping.initialize(mvcApplicationContext);
            }
        }
        this.initialized = true;
    }

    public void service(WebRequest request, WebResponse response) {
        ensureInitialized();
        Object handler = null;
        try {
            HandlerExecutionChain executionChain = getHandler(request);
            if (executionChain == null) {
                throw new NoHandlerFoundException(request.getMethod(), request.getRequestUri());
            }
            handler = executionChain.getHandler();
            HandlerAdapter handlerAdapter = getHandlerAdapter(handler);
            handlerAdapter.handle(request, response, handler);
        } catch (NoHandlerAdapterException | HandlerAdapterConflictException ex) {
            throw ex;
        } catch (Exception ex) {
            processDispatchException(request, response, handler, ex);
        }
    }

    public List<HandlerMapping> getHandlerMappings() {
        return handlerMappings;
    }

    public List<HandlerAdapter> getHandlerAdapters() {
        return handlerAdapters;
    }

    public List<ExceptionResolver> getExceptionResolvers() {
        return exceptionResolvers;
    }

    private void ensureInitialized() {
        if (!initialized) {
            throw new MvcException("DispatcherServlet has not been initialized");
        }
    }

    private HandlerExecutionChain getHandler(WebRequest request) {
        for (HandlerMapping handlerMapping : handlerMappings) {
            HandlerExecutionChain executionChain = handlerMapping.getHandler(request);
            if (executionChain != null) {
                return executionChain;
            }
        }
        return null;
    }

    private HandlerAdapter getHandlerAdapter(Object handler) {
        List<HandlerAdapter> supportingAdapters = new ArrayList<>();
        for (HandlerAdapter handlerAdapter : handlerAdapters) {
            if (handlerAdapter.supports(handler)) {
                supportingAdapters.add(handlerAdapter);
            }
        }
        if (supportingAdapters.isEmpty()) {
            throw new NoHandlerAdapterException(handler.getClass().getName());
        }
        if (supportingAdapters.size() > 1) {
            String candidates = supportingAdapters.stream()
                    .map(adapter -> adapter.getClass().getName())
                    .collect(Collectors.joining(", "));
            throw new HandlerAdapterConflictException(handler.getClass().getName(), supportingAdapters.size(), candidates);
        }
        return supportingAdapters.get(0);
    }

    private void processDispatchException(WebRequest request, WebResponse response, Object handler, Exception ex) {
        for (ExceptionResolver exceptionResolver : exceptionResolvers) {
            try {
                if (exceptionResolver.resolveException(request, response, handler, ex)) {
                    return;
                }
            } catch (Exception resolverEx) {
                throw new MvcException("Failed to resolve exception [" + ex.getMessage() + "]", resolverEx);
            }
        }
        throw new MvcException("Unresolved MVC exception [" + ex.getMessage() + "]", ex);
    }
}
