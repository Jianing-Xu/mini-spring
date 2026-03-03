package com.xujn.minispring.test.phase2.bpp;

import java.util.ArrayList;
import java.util.List;

public final class TrackingState {

    public static final List<String> BEFORE = new ArrayList<>();
    public static final List<String> AFTER = new ArrayList<>();
    public static final List<String> ORDER = new ArrayList<>();

    private TrackingState() {
    }

    public static void reset() {
        BEFORE.clear();
        AFTER.clear();
        ORDER.clear();
    }
}
