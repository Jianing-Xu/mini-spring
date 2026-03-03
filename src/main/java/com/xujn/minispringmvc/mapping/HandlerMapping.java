package com.xujn.minispringmvc.mapping;

import com.xujn.minispringmvc.servlet.HandlerExecutionChain;
import com.xujn.minispringmvc.servlet.WebRequest;

/**
 * Strategy interface for selecting a handler for the current request.
 * Constraint: Phase 1 returns at most one handler execution chain and uses exact method/path matching.
 * Thread-safety: implementations are initialized during bootstrap and then read-only during dispatch.
 */
public interface HandlerMapping {

    HandlerExecutionChain getHandler(WebRequest request);
}
