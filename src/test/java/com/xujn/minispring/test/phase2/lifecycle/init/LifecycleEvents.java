package com.xujn.minispring.test.phase2.lifecycle.init;

import java.util.ArrayList;
import java.util.List;

public final class LifecycleEvents {

    public static final List<String> EVENTS = new ArrayList<>();

    private LifecycleEvents() {
    }

    public static void reset() {
        EVENTS.clear();
    }
}
