package com.xujn.minispringmvc.adapter;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.context.MvcApplicationContext;
import com.xujn.minispringmvc.adapter.support.InvocableHandlerMethod;
import com.xujn.minispringmvc.adapter.support.HandlerMethodArgumentResolverComposite;
import com.xujn.minispringmvc.adapter.support.HandlerMethodReturnValueHandlerComposite;
import com.xujn.minispringmvc.adapter.support.ModelAndViewReturnValueHandler;
import com.xujn.minispringmvc.adapter.support.StringReturnValueHandler;
import com.xujn.minispringmvc.adapter.support.ViewNameReturnValueHandler;
import com.xujn.minispringmvc.mapping.HandlerMethod;
import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;
import com.xujn.minispringmvc.support.Ordered;
import com.xujn.minispringmvc.context.support.DefaultMvcInfrastructureInitializer;
import com.xujn.minispringmvc.view.ViewResolver;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default adapter that executes HandlerMethod instances.
 * Constraint: Phase 2 initializes ordered argument resolvers and return value handlers from the container.
 * Thread-safety: initialized once and then read-only.
 */
@Component
public class RequestMappingHandlerAdapter implements HandlerAdapter, Ordered {

    private final DefaultMvcInfrastructureInitializer infrastructureInitializer = new DefaultMvcInfrastructureInitializer();
    private final HandlerMethodArgumentResolverComposite argumentResolvers = new HandlerMethodArgumentResolverComposite();
    private final HandlerMethodReturnValueHandlerComposite returnValueHandlers = new HandlerMethodReturnValueHandlerComposite();
    private boolean viewResolutionEnabled;
    private boolean initialized;

    public void initialize(MvcApplicationContext context) {
        if (initialized) {
            return;
        }
        viewResolutionEnabled = !context.getBeansOfType(ViewResolver.class).isEmpty();
        argumentResolvers.addResolvers(
                infrastructureInitializer.initializeBeans(context, HandlerMethodArgumentResolver.class));
        returnValueHandlers.addHandlers(filterReturnValueHandlers(
                infrastructureInitializer.initializeBeans(context, HandlerMethodReturnValueHandler.class)));
        initialized = true;
    }

    public HandlerMethodArgumentResolverComposite getArgumentResolvers() {
        return argumentResolvers;
    }

    public HandlerMethodReturnValueHandlerComposite getReturnValueHandlers() {
        return returnValueHandlers;
    }

    public boolean isViewResolutionEnabled() {
        return viewResolutionEnabled;
    }

    @Override
    public boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public ModelAndView handle(WebRequest request, WebResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (!initialized) {
            throw new IllegalStateException("RequestMappingHandlerAdapter has not been initialized");
        }
        return new InvocableHandlerMethod(handlerMethod, argumentResolvers, returnValueHandlers).invokeForRequest(request, response);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private List<HandlerMethodReturnValueHandler> filterReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        return handlers.stream()
                .filter(handler -> viewResolutionEnabled
                        ? !(handler instanceof StringReturnValueHandler)
                        : !(handler instanceof ViewNameReturnValueHandler || handler instanceof ModelAndViewReturnValueHandler))
                .collect(Collectors.toUnmodifiableList());
    }
}
