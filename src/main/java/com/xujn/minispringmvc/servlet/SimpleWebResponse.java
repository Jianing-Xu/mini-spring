package com.xujn.minispringmvc.servlet;

import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mutable response implementation for tests and examples.
 * Constraint: Phase 1 treats any body write as a committed response.
 * Thread-safety: response mutation is not concurrent-safe and is intended for a single request thread.
 */
public class SimpleWebResponse implements WebResponse {

    private int status = 200;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final StringWriter writer = new StringWriter();
    private boolean committed;

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Writer getWriter() {
        return writer;
    }

    @Override
    public void write(String content) {
        if (content != null) {
            writer.write(content);
        }
        committed = true;
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    @Override
    public String getBodyAsString() {
        return writer.toString();
    }
}
