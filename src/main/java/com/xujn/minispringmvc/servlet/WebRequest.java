package com.xujn.minispringmvc.servlet;

import java.io.InputStream;
import java.util.Map;

/**
 * MVC request abstraction decoupled from any concrete servlet container.
 * Constraint: Phase 1 exposes only request data required for exact mapping and simple parameter binding.
 * Thread-safety: request objects are single-request scoped and not designed for concurrent mutation.
 */
public interface WebRequest {

    String getMethod();

    String getRequestUri();

    String getContextPath();

    String getParameter(String name);

    Map<String, String[]> getParameters();

    String getHeader(String name);

    Map<String, String> getHeaders();

    InputStream getBodyStream();

    Object getAttribute(String name);

    void setAttribute(String name, Object value);
}
