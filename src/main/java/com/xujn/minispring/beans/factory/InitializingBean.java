package com.xujn.minispring.beans.factory;

/**
 * Callback contract invoked after dependency injection and before after-initialization post processors.
 * Constraint: invoked once per bean instance created by the container.
 * Thread-safety: depends on the implementing bean.
 */
public interface InitializingBean {

    void afterPropertiesSet();
}
