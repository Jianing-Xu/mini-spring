package com.xujn.minispringmvc.support;

/**
 * Ordering contract for MVC extension points.
 * Constraint: lower values have higher priority during MVC init sorting.
 * Thread-safety: implementations are expected to be stateless or bootstrap-configured.
 */
public interface Ordered {

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    int getOrder();
}
