package com.xujn.minispring.test.phase2.lifecycle.destroy;

public final class DisposableState {

    public static boolean disposableDestroyed;
    public static boolean secondDestroyed;
    public static boolean thirdDestroyed;
    public static boolean normalDestroyed;
    public static boolean prototypeDestroyed;
    public static boolean lifecycleDestroyed;

    private DisposableState() {
    }

    public static void reset() {
        disposableDestroyed = false;
        secondDestroyed = false;
        thirdDestroyed = false;
        normalDestroyed = false;
        prototypeDestroyed = false;
        lifecycleDestroyed = false;
    }
}
