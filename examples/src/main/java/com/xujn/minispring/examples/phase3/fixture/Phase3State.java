package com.xujn.minispring.examples.phase3.fixture;

public final class Phase3State {

    public static boolean intercepted;

    private Phase3State() {
    }

    public static void reset() {
        intercepted = false;
    }
}
