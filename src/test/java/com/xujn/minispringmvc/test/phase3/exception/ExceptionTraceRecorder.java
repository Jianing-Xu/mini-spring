package com.xujn.minispringmvc.test.phase3.exception;

import com.xujn.minispring.context.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExceptionTraceRecorder {

    private final List<String> events = new ArrayList<>();

    public void record(String event) {
        events.add(event);
    }

    public List<String> getEvents() {
        return events;
    }
}
