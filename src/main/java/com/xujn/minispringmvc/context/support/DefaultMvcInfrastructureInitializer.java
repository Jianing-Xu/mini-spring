package com.xujn.minispringmvc.context.support;

import com.xujn.minispringmvc.context.MvcApplicationContext;
import com.xujn.minispringmvc.support.Ordered;
import com.xujn.minispringmvc.support.PriorityOrdered;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Collects and sorts MVC infrastructure beans from the mini-spring container.
 * Constraint: lists are built once during Dispatcher init and treated as immutable afterward.
 * Thread-safety: bootstrap-only utility, not designed for concurrent mutation.
 */
public class DefaultMvcInfrastructureInitializer {

    public <T> List<T> initializeBeans(MvcApplicationContext context, Class<T> type) {
        List<T> beans = new ArrayList<>(context.getBeansOfType(type));
        beans.sort(componentComparator());
        return List.copyOf(beans);
    }

    private Comparator<Object> componentComparator() {
        return Comparator
                .comparingInt((Object bean) -> bean instanceof PriorityOrdered ? 0 : bean instanceof Ordered ? 1 : 2)
                .thenComparingInt(bean -> bean instanceof Ordered ordered ? ordered.getOrder() : Ordered.LOWEST_PRECEDENCE)
                .thenComparing(bean -> bean.getClass().getName());
    }
}
