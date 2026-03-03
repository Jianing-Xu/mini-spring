package com.xujn.minispring.test.javaconfig.phase2.lifecycle;

public final class LifecyclePhase2State {

    public static boolean customInitCalled;
    public static boolean interfaceDestroyCalled;
    public static boolean customDestroyCalled;

    private LifecyclePhase2State() {
    }

    public static void reset() {
        customInitCalled = false;
        interfaceDestroyCalled = false;
        customDestroyCalled = false;
    }
}
