package com.xujn.minispringmvc.servlet;

import java.io.Writer;
import java.util.Map;

/**
 * MVC response abstraction used by Dispatcher and return value handlers.
 * Constraint: Phase 1 only needs status, headers, a writer, and committed tracking.
 * Thread-safety: response objects are single-request scoped and not designed for concurrent mutation.
 */
public interface WebResponse {

    int getStatus();

    void setStatus(int status);

    void setHeader(String name, String value);

    String getHeader(String name);

    Map<String, String> getHeaders();

    Writer getWriter();

    void write(String content);

    boolean isCommitted();

    String getBodyAsString();
}
