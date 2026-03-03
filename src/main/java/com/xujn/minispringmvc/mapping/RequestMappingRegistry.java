package com.xujn.minispringmvc.mapping;

import com.xujn.minispringmvc.exception.MappingConflictException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores annotation-derived request mappings for Phase 1 exact lookups.
 * Constraint: duplicate method/path keys are rejected during init to keep dispatch deterministic.
 * Thread-safety: populated during bootstrap and then read-only.
 */
public class RequestMappingRegistry {

    private final Map<RequestMappingInfo, HandlerMethod> mappings = new LinkedHashMap<>();

    public void register(RequestMappingInfo mappingInfo, HandlerMethod handlerMethod) {
        HandlerMethod existing = mappings.get(mappingInfo);
        if (existing != null) {
            throw new MappingConflictException(mappingInfo, existing.getShortLogMessage(), handlerMethod.getShortLogMessage());
        }
        mappings.put(mappingInfo, handlerMethod);
    }

    public HandlerMethod getHandlerMethod(String httpMethod, String path) {
        return mappings.get(new RequestMappingInfo(httpMethod, path));
    }

    public int size() {
        return mappings.size();
    }
}
