package com.xujn.minispringmvc.servlet;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Carries a logical view name together with model attributes for view rendering.
 * Constraint: Phase 3 uses an empty ModelAndView instance to represent "resolved without view rendering".
 * Thread-safety: request-scoped mutable state, not designed for concurrent mutation.
 */
public class ModelAndView {

    private static final ModelAndView EMPTY = new ModelAndView();

    private final String viewName;
    private final Map<String, Object> model = new LinkedHashMap<>();

    private ModelAndView() {
        this.viewName = null;
    }

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public static ModelAndView empty() {
        return EMPTY;
    }

    public String getViewName() {
        return viewName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public ModelAndView addObject(String attributeName, Object attributeValue) {
        model.put(attributeName, attributeValue);
        return this;
    }

    public boolean hasView() {
        return viewName != null && !viewName.isBlank();
    }

    public boolean isEmpty() {
        return this == EMPTY || (!hasView() && model.isEmpty());
    }
}
