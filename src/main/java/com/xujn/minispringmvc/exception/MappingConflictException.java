package com.xujn.minispringmvc.exception;

import com.xujn.minispringmvc.mapping.RequestMappingInfo;

/**
 * Raised when two controller methods declare the same request mapping.
 * Constraint: conflict messages include method/path and both handler candidates.
 * Thread-safety: immutable exception state after construction.
 */
public class MappingConflictException extends MvcException {

    public MappingConflictException(RequestMappingInfo mappingInfo, String existingHandler, String newHandler) {
        super("Mapping conflict for [" + mappingInfo + "], existing handler [" + existingHandler +
                "], new handler [" + newHandler + "]");
    }
}
