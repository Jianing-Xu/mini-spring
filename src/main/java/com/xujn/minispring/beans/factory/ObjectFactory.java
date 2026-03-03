package com.xujn.minispring.beans.factory;

/**
 * Lazy object factory used by the third-level singleton cache.
 * Constraint: in Phase 3 it is used only to expose early singleton references.
 * Thread-safety: depends on the backing implementation.
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    T getObject();
}
