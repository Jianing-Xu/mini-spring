package com.xujn.minispringmvc.adapter.support;

/**
 * Minimal string-based converter for Phase 1 request parameter binding.
 * Constraint: only String, int/Integer, long/Long, and boolean/Boolean are supported.
 * Thread-safety: stateless and thread-safe.
 */
public class SimpleTypeConverter {

    public boolean supports(Class<?> targetType) {
        return targetType == String.class
                || targetType == int.class
                || targetType == Integer.class
                || targetType == long.class
                || targetType == Long.class
                || targetType == boolean.class
                || targetType == Boolean.class;
    }

    public Object convert(String rawValue, Class<?> targetType) {
        if (targetType == String.class) {
            return rawValue;
        }
        if (targetType == int.class || targetType == Integer.class) {
            return Integer.parseInt(rawValue);
        }
        if (targetType == long.class || targetType == Long.class) {
            return Long.parseLong(rawValue);
        }
        if (targetType == boolean.class || targetType == Boolean.class) {
            return Boolean.parseBoolean(rawValue);
        }
        throw new IllegalArgumentException("Unsupported target type " + targetType.getName());
    }
}
