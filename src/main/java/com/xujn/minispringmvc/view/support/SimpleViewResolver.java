package com.xujn.minispringmvc.view.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.context.MvcApplicationContext;
import com.xujn.minispringmvc.support.Ordered;
import com.xujn.minispringmvc.view.View;
import com.xujn.minispringmvc.view.ViewResolver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple resolver that maps a logical view name to container-managed View beans.
 * Constraint: Phase 3 resolves either an exact bean name or a bean name with the "View" suffix.
 * Thread-safety: initialized during bootstrap and then read-only.
 */
@Component
public class SimpleViewResolver implements ViewResolver, Ordered {

    private final Map<String, View> views = new LinkedHashMap<>();
    private boolean initialized;

    public void initialize(MvcApplicationContext context) {
        if (initialized) {
            return;
        }
        for (String beanName : context.getBeanNamesForType(View.class)) {
            views.put(beanName, context.getBean(beanName, View.class));
        }
        initialized = true;
    }

    @Override
    public boolean supports(String viewName) {
        return views.containsKey(viewName) || views.containsKey(viewName + "View");
    }

    @Override
    public View resolveViewName(String viewName) {
        View view = views.get(viewName);
        if (view != null) {
            return view;
        }
        return views.get(viewName + "View");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
