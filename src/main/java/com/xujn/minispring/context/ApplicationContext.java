package com.xujn.minispring.context;

import com.xujn.minispring.beans.factory.BeanFactory;

/**
 * High-level container contract with refresh and close semantics.
 * Constraint: Phase 1 close is a no-op and refresh may run only once.
 * Thread-safety: implementations are not designed for concurrent refresh.
 */
public interface ApplicationContext extends BeanFactory {

    void refresh();

    void close();
}
