package com.xujn.minispring.tx.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread-local storage for transaction resources and rollback-only markers.
 * Constraint: designed for single-threaded call chains without async handoff.
 * Thread-safety: thread-confined via ThreadLocal.
 */
public final class TransactionSynchronizationManager {

    private static final ThreadLocal<Map<Object, Object>> RESOURCES =
            ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<Object, Boolean>> ROLLBACK_ONLY =
            ThreadLocal.withInitial(HashMap::new);

    private TransactionSynchronizationManager() {
    }

    public static boolean hasResource(Object key) {
        return RESOURCES.get().containsKey(key);
    }

    public static Object getResource(Object key) {
        return RESOURCES.get().get(key);
    }

    public static void bindResource(Object key, Object value) {
        RESOURCES.get().put(key, value);
    }

    public static Object unbindResource(Object key) {
        Object removed = RESOURCES.get().remove(key);
        if (RESOURCES.get().isEmpty()) {
            RESOURCES.remove();
        }
        return removed;
    }

    public static void setRollbackOnly(Object key) {
        ROLLBACK_ONLY.get().put(key, Boolean.TRUE);
    }

    public static boolean isRollbackOnly(Object key) {
        return Boolean.TRUE.equals(ROLLBACK_ONLY.get().get(key));
    }

    public static void clearRollbackOnly(Object key) {
        ROLLBACK_ONLY.get().remove(key);
        if (ROLLBACK_ONLY.get().isEmpty()) {
            ROLLBACK_ONLY.remove();
        }
    }

    public static void clear() {
        RESOURCES.remove();
        ROLLBACK_ONLY.remove();
    }
}
