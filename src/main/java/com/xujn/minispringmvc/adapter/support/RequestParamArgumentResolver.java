package com.xujn.minispringmvc.adapter.support;

import com.xujn.minispringmvc.adapter.HandlerMethodArgumentResolver;
import com.xujn.minispringmvc.annotation.RequestParam;
import com.xujn.minispringmvc.exception.MethodArgumentTypeMismatchException;
import com.xujn.minispringmvc.exception.MissingRequestParameterException;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Resolves @RequestParam annotated arguments from request parameters.
 * Constraint: Phase 1 requires explicit parameter names and simple target types only.
 * Thread-safety: stateless after construction.
 */
public class RequestParamArgumentResolver implements HandlerMethodArgumentResolver {

    private final SimpleTypeConverter typeConverter = new SimpleTypeConverter();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, WebRequest request, WebResponse response) {
        RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
        String parameterName = requestParam.value();
        String rawValue = request.getParameter(parameterName);
        if ((rawValue == null || rawValue.isBlank()) && requestParam.required()) {
            throw new MissingRequestParameterException(parameterName, parameter.getMethod());
        }
        if (rawValue == null) {
            return null;
        }
        Class<?> targetType = parameter.getParameterType();
        if (!typeConverter.supports(targetType)) {
            throw new MethodArgumentTypeMismatchException(parameterName, targetType, rawValue, parameter.getMethod());
        }
        try {
            return typeConverter.convert(rawValue, targetType);
        } catch (RuntimeException ex) {
            throw new MethodArgumentTypeMismatchException(parameterName, targetType, rawValue, parameter.getMethod(), ex);
        }
    }
}
