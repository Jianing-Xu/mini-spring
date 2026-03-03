package com.xujn.minispringmvc.servlet;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Mutable request implementation for tests and examples.
 * Constraint: Phase 1 models only exact-path request dispatch and simple query parameters.
 * Thread-safety: request mutation is not concurrent-safe and is intended for single-threaded request setup.
 */
public class SimpleWebRequest implements WebRequest {

    private final String method;
    private final String requestUri;
    private final String contextPath;
    private final Map<String, String[]> parameters = new LinkedHashMap<>();
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final Map<String, Object> attributes = new LinkedHashMap<>();
    private InputStream bodyStream = new ByteArrayInputStream(new byte[0]);

    public SimpleWebRequest(String method, String requestUri) {
        this(method, requestUri, "");
    }

    public SimpleWebRequest(String method, String requestUri, String contextPath) {
        this.method = Objects.requireNonNull(method, "method must not be null").toUpperCase();
        this.requestUri = Objects.requireNonNull(requestUri, "requestUri must not be null");
        this.contextPath = contextPath == null ? "" : contextPath;
    }

    public static SimpleWebRequest get(String requestUri) {
        return new SimpleWebRequest("GET", requestUri);
    }

    public static SimpleWebRequest post(String requestUri) {
        return new SimpleWebRequest("POST", requestUri);
    }

    public SimpleWebRequest addParameter(String name, String value) {
        parameters.put(name, new String[]{value});
        return this;
    }

    public SimpleWebRequest addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public SimpleWebRequest setBody(String body) {
        String safeBody = body == null ? "" : body;
        this.bodyStream = new ByteArrayInputStream(safeBody.getBytes(StandardCharsets.UTF_8));
        return this;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getRequestUri() {
        return requestUri;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameters.get(name);
        return values == null || values.length == 0 ? null : values[0];
    }

    @Override
    public Map<String, String[]> getParameters() {
        return parameters;
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
    public InputStream getBodyStream() {
        return bodyStream;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
}
