package com.xujn.minispringmvc.servlet;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispringmvc.adapter.HandlerAdapter;
import com.xujn.minispringmvc.context.MvcApplicationContext;
import com.xujn.minispringmvc.context.support.DefaultMvcInfrastructureInitializer;
import com.xujn.minispringmvc.exception.ExceptionResolver;
import com.xujn.minispringmvc.exception.HandlerAdapterConflictException;
import com.xujn.minispringmvc.exception.HandlerExceptionResolverComposite;
import com.xujn.minispringmvc.exception.MvcException;
import com.xujn.minispringmvc.exception.NoHandlerAdapterException;
import com.xujn.minispringmvc.exception.NoHandlerFoundException;
import com.xujn.minispringmvc.exception.UnsupportedHandlerMethodParameterException;
import com.xujn.minispringmvc.exception.UnsupportedHandlerMethodReturnValueException;
import com.xujn.minispringmvc.mapping.HandlerMapping;
import com.xujn.minispringmvc.mapping.RequestMappingHandlerMapping;
import com.xujn.minispringmvc.view.View;
import com.xujn.minispringmvc.view.ViewResolver;
import com.xujn.minispringmvc.view.support.SimpleViewResolver;

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
    private List<ViewResolver> viewResolvers = List.of();
    private final HandlerExceptionResolverComposite exceptionResolverComposite = new HandlerExceptionResolverComposite();
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
        this.viewResolvers = infrastructureInitializer.initializeBeans(mvcApplicationContext, ViewResolver.class);
        exceptionResolverComposite.addResolvers(exceptionResolvers);
        for (HandlerMapping handlerMapping : handlerMappings) {
            if (handlerMapping instanceof RequestMappingHandlerMapping requestMappingHandlerMapping) {
                requestMappingHandlerMapping.initialize(mvcApplicationContext);
            }
        }
        for (HandlerAdapter handlerAdapter : handlerAdapters) {
            if (handlerAdapter instanceof com.xujn.minispringmvc.adapter.RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
                requestMappingHandlerAdapter.initialize(mvcApplicationContext);
            }
        }
        for (ViewResolver viewResolver : viewResolvers) {
            if (viewResolver instanceof SimpleViewResolver simpleViewResolver) {
                simpleViewResolver.initialize(mvcApplicationContext);
            }
        }
        this.initialized = true;
    }

    public void service(WebRequest request, WebResponse response) {
        ensureInitialized();
        HandlerExecutionChain executionChain = null;
        ModelAndView modelAndView = null;
        Exception dispatchException = null;
        try {
            executionChain = getHandler(request);
            if (executionChain == null) {
                throw new NoHandlerFoundException(request.getMethod(), request.getRequestUri());
            }
            if (!executionChain.applyPreHandle(request, response)) {
                return;
            }
            Object handler = executionChain.getHandler();
            HandlerAdapter handlerAdapter = getHandlerAdapter(handler);
            modelAndView = handlerAdapter.handle(request, response, handler);
            executionChain.applyPostHandle(request, response, modelAndView);
            renderModelAndView(modelAndView, request, response);
        } catch (NoHandlerAdapterException
                 | HandlerAdapterConflictException
                 | UnsupportedHandlerMethodParameterException
                 | UnsupportedHandlerMethodReturnValueException ex) {
            throw ex;
        } catch (Exception ex) {
            dispatchException = ex;
            modelAndView = processDispatchException(request, response, executionChain == null ? null : executionChain.getHandler(), ex);
            if (modelAndView != null && !response.isCommitted()) {
                try {
                    renderModelAndView(modelAndView, request, response);
                } catch (Exception renderEx) {
                    dispatchException = renderEx;
                    if (!response.isCommitted()) {
                        processDispatchException(request, response, executionChain == null ? null : executionChain.getHandler(), renderEx);
                    }
                }
            }
        } finally {
            if (executionChain != null) {
                try {
                    executionChain.triggerAfterCompletion(request, response, dispatchException);
                } catch (Exception ex) {
                    throw new MvcException("Failed during afterCompletion: " + ex.getMessage(), ex);
                }
            }
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

    public List<ViewResolver> getViewResolvers() {
        return viewResolvers;
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

    private ModelAndView processDispatchException(
            WebRequest request, WebResponse response, Object handler, Exception ex) {
        try {
            ModelAndView modelAndView = exceptionResolverComposite.resolveException(request, response, handler, ex);
            if (modelAndView != null) {
                return modelAndView;
            }
        } catch (Exception resolverEx) {
            throw new MvcException("Failed to resolve exception [" + ex.getMessage() + "]", resolverEx);
        }
        throw new MvcException("Unresolved MVC exception [" + ex.getMessage() + "]", ex);
    }

    private void renderModelAndView(ModelAndView modelAndView, WebRequest request, WebResponse response) throws Exception {
        if (modelAndView == null || modelAndView.isEmpty() || response.isCommitted()) {
            return;
        }
        if (!modelAndView.hasView()) {
            return;
        }
        View view = resolveView(modelAndView.getViewName());
        if (view == null) {
            throw new MvcException("No ViewResolver for view name [" + modelAndView.getViewName() + "]");
        }
        view.render(modelAndView.getModel(), request, response);
    }

    private View resolveView(String viewName) throws Exception {
        for (ViewResolver viewResolver : viewResolvers) {
            if (viewResolver.supports(viewName)) {
                return viewResolver.resolveViewName(viewName);
            }
        }
        return null;
    }
}
