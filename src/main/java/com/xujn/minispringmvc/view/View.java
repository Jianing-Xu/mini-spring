package com.xujn.minispringmvc.view;

import com.xujn.minispringmvc.servlet.WebRequest;
import com.xujn.minispringmvc.servlet.WebResponse;

import java.util.Map;

/**
 * Renders a view model into the current response.
 * Constraint: Phase 3 keeps rendering synchronous and request-scoped.
 * Thread-safety: view implementations are expected to be stateless after bootstrap.
 */
public interface View {

    void render(Map<String, Object> model, WebRequest request, WebResponse response) throws Exception;
}
