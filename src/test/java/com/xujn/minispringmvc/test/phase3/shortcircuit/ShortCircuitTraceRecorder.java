package com.xujn.minispringmvc.test.phase3.shortcircuit;

import com.xujn.minispring.context.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShortCircuitTraceRecorder {

    private final List<String> events = new ArrayList<>();

    public void record(String event) {
        events.add(event);
    }

    public List<String> getEvents() {
        return events;
    }
}
