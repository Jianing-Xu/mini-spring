package com.xujn.minispringmvc.bind.support;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispringmvc.bind.TypeConverter;

/**
 * Minimal scalar converter for request parameter binding.
 * Constraint: supports String, int/Integer, long/Long, and boolean/Boolean only.
 * Thread-safety: stateless and thread-safe.
 */
@Component
public class SimpleTypeConverter implements TypeConverter {

    @Override
    public boolean supports(Class<?> targetType) {
        return targetType == String.class
                || targetType == int.class
                || targetType == Integer.class
                || targetType == long.class
                || targetType == Long.class
                || targetType == boolean.class
                || targetType == Boolean.class;
    }

    @Override
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
