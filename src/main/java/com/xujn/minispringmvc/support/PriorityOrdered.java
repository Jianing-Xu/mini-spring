package com.xujn.minispringmvc.support;

/**
 * Marker contract for MVC extensions that must run before regular Ordered components.
 * Constraint: Dispatcher init sorts PriorityOrdered components ahead of Ordered components.
 * Thread-safety: marker interface, no mutable state.
 */
public interface PriorityOrdered extends Ordered {
}
