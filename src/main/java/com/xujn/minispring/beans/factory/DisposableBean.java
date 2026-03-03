package com.xujn.minispring.beans.factory;

/**
 * Callback contract invoked when the application context is closed.
 * Constraint: Phase 2 only invokes destroy on singleton beans managed by the container.
 * Thread-safety: depends on the implementing bean.
 */
public interface DisposableBean {

    void destroy();
}
