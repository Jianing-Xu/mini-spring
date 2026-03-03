package com.xujn.minispringmvc.exception;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.mapping.HandlerMethod;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Default exception resolver for Phase 1 dispatch failures.
 * Constraint: resolves 404, 400, and generic 500 responses with contextual error messages.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class DefaultHandlerExceptionResolver implements ExceptionResolver {

    @Override
    public boolean resolveException(WebRequest request, WebResponse response, Object handler, Exception ex) {
        if (response.isCommitted()) {
            return true;
        }
        if (ex instanceof NoHandlerFoundException) {
            response.setStatus(404);
            response.write(ex.getMessage());
            return true;
        }
        if (ex instanceof MissingRequestParameterException || ex instanceof MethodArgumentTypeMismatchException) {
            response.setStatus(400);
            response.write(ex.getMessage());
            return true;
        }
        response.setStatus(500);
        response.write("Handler [" + describeHandler(handler) + "] failed: " + ex.getMessage());
        return true;
    }

    private String describeHandler(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getShortLogMessage();
        }
        return handler == null ? "unknown" : handler.getClass().getName();
    }
}
