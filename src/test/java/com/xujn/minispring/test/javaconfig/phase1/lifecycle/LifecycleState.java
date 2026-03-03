package com.xujn.minispring.test.javaconfig.phase1.lifecycle;

public final class LifecycleState {

    public static boolean dependencyVisibleDuringInit;
    public static boolean beforeInitializationCalled;
    public static boolean afterInitializationCalled;

    private LifecycleState() {
    }

    public static void reset() {
        dependencyVisibleDuringInit = false;
        beforeInitializationCalled = false;
        afterInitializationCalled = false;
    }
}
