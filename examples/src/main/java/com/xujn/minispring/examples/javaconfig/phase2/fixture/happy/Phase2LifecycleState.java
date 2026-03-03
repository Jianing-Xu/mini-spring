package com.xujn.minispring.examples.javaconfig.phase2.fixture.happy;

public final class Phase2LifecycleState {

    public static boolean customInitCalled;
    public static boolean customDestroyCalled;

    private Phase2LifecycleState() {
    }

    public static void reset() {
        customInitCalled = false;
        customDestroyCalled = false;
    }
}
