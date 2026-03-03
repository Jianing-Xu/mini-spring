package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispringmvc.adapter.HandlerMethodArgumentResolver;
import com.xujn.minispringmvc.adapter.HandlerMethodReturnValueHandler;
import com.xujn.minispringmvc.exception.MvcException;
import com.xujn.minispringmvc.mapping.HandlerMethod;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Coordinates argument resolution, reflective invocation, and return value handling for a HandlerMethod.
 * Constraint: Phase 1 uses simple ordered lists instead of composite extension chains.
 * Thread-safety: immutable after construction and safe for concurrent reads.
 */
public class InvocableHandlerMethod {

    private final HandlerMethod handlerMethod;
    private final HandlerMethodArgumentResolverComposite argumentResolvers;
    private final HandlerMethodReturnValueHandlerComposite returnValueHandlers;

    public InvocableHandlerMethod(
            HandlerMethod handlerMethod,
            HandlerMethodArgumentResolverComposite argumentResolvers,
            HandlerMethodReturnValueHandlerComposite returnValueHandlers) {
        this.handlerMethod = handlerMethod;
        this.argumentResolvers = argumentResolvers;
        this.returnValueHandlers = returnValueHandlers;
    }

    public void invokeForRequest(WebRequest request, WebResponse response) throws Exception {
        Object[] arguments = getMethodArgumentValues(request, response);
        Method invocableMethod = handlerMethod.getInvocableMethod();
        Object returnValue;
        try {
            returnValue = invocableMethod.invoke(handlerMethod.getBean(), arguments);
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof Exception exception) {
                throw exception;
            }
            throw new RuntimeException(targetException);
        } catch (IllegalAccessException ex) {
            throw new MvcException("Failed to invoke handler [" + handlerMethod.getShortLogMessage() + "]", ex);
        }
        handleReturnValue(returnValue, request, response);
    }

    private Object[] getMethodArgumentValues(WebRequest request, WebResponse response) throws Exception {
        MethodParameter[] parameters = handlerMethod.getParameters();
        Object[] arguments = new Object[parameters.length];
        for (int index = 0; index < parameters.length; index++) {
            MethodParameter parameter = parameters[index];
            arguments[index] = resolveArgument(parameter, request, response);
        }
        return arguments;
    }

    private Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) throws Exception {
        return argumentResolvers.resolveArgument(parameter, request, response);
    }

    private void handleReturnValue(Object returnValue, WebRequest request, WebResponse response) throws Exception {
        returnValueHandlers.handleReturnValue(returnValue, handlerMethod.getReturnType(), request, response);
    }
}
