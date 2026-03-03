package com.xujn.minispring.test.phase2.lifecycle.aop;

public final class AopLifecycleState {

    public static boolean initialized;
    public static boolean destroyed;

    private AopLifecycleState() {
    }

    public static void reset() {
        initialized = false;
        destroyed = false;
    }
}
