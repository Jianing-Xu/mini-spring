package com.xujn.minispringmvc.exception;

import com.xujn.minispringmvc.servlet.ModelAndView;
import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered exception resolver chain that stops at the first matching resolver.
 * Constraint: Phase 3 treats a non-null ModelAndView as "resolved" even if it carries no view.
 * Thread-safety: populated during init and then treated as immutable.
 */
public final class HandlerExceptionResolverComposite {

    private final List<ExceptionResolver> resolvers = new ArrayList<>();

    public void addResolvers(List<ExceptionResolver> resolvers) {
        this.resolvers.clear();
        this.resolvers.addAll(resolvers);
    }

    public List<ExceptionResolver> getResolvers() {
        return List.copyOf(resolvers);
    }

    public ModelAndView resolveException(
            WebRequest request, WebResponse response, Object handler, Exception ex) throws Exception {
        for (ExceptionResolver resolver : resolvers) {
            if (!resolver.supports(ex, handler)) {
                continue;
            }
            ModelAndView modelAndView = resolver.resolveException(request, response, handler, ex);
            if (modelAndView != null) {
                return modelAndView;
            }
        }
        return null;
    }
}
