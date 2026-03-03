package com.xujn.minispring.examples.phase2.fixture;

public final class ExampleState {

    public static boolean initialized;
    public static boolean destroyed;
    public static boolean intercepted;

    private ExampleState() {
    }

    public static void reset() {
        initialized = false;
        destroyed = false;
        intercepted = false;
    }
}
