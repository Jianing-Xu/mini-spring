package com.xujn.minispringmvc.view;

/**
 * Resolves logical view names into concrete View instances.
 * Constraint: Phase 3 stops at the first resolver that supports a view name.
 * Thread-safety: view resolvers are initialized during bootstrap and then treated as read-only.
 */
public interface ViewResolver {

    boolean supports(String viewName);

    View resolveViewName(String viewName) throws Exception;
}
