package com.xujn.minispringmvc.exception;

import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

/**
 * Resolves dispatch exceptions into a stable HTTP-like response outcome.
 * Constraint: Phase 1 uses a single default resolver and does not expose a full resolver chain.
 * Thread-safety: resolvers are stateless after bootstrap.
 */
public interface ExceptionResolver {

    boolean resolveException(WebRequest request, WebResponse response, Object handler, Exception ex) throws Exception;
}
