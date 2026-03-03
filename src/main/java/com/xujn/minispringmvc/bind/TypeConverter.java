package com.xujn.minispringmvc.bind;

/**
 * Converts raw request values into target Java types for MVC argument binding.
 * Constraint: Phase 2 keeps conversion string-based and limited to simple scalar types.
 * Thread-safety: implementations are expected to be stateless after bootstrap.
 */
public interface TypeConverter {

    boolean supports(Class<?> targetType);

    Object convert(String rawValue, Class<?> targetType) throws Exception;
}
