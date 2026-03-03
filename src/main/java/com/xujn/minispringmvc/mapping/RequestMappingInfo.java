package com.xujn.minispringmvc.mapping;

import java.util.Locale;
import java.util.Objects;

/**
 * Immutable lookup key for an annotation-driven handler mapping.
 * Constraint: Phase 1 uses only HTTP method and normalized exact path.
 * Thread-safety: immutable after construction.
 */
public final class RequestMappingInfo {

    private final String httpMethod;
    private final String path;

    public RequestMappingInfo(String httpMethod, String path) {
        this.httpMethod = normalizeMethod(httpMethod);
        this.path = normalizePath(path);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public static String normalizeMethod(String method) {
        return Objects.requireNonNull(method, "method must not be null").trim().toUpperCase(Locale.ROOT);
    }

    public static String normalizePath(String path) {
        String candidate = Objects.requireNonNull(path, "path must not be null").trim();
        if (candidate.isEmpty() || "/".equals(candidate)) {
            return "/";
        }
        String normalized = candidate.startsWith("/") ? candidate : "/" + candidate;
        while (normalized.contains("//")) {
            normalized = normalized.replace("//", "/");
        }
        if (normalized.length() > 1 && normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RequestMappingInfo that)) {
            return false;
        }
        return httpMethod.equals(that.httpMethod) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path);
    }

    @Override
    public String toString() {
        return httpMethod + " " + path;
    }
}
