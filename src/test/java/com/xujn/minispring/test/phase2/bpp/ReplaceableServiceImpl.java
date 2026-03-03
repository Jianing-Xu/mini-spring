package com.xujn.minispring.test.phase2.bpp;

import com.xujn.minispring.context.annotation.Component;

@Component
public class ReplaceableServiceImpl implements ReplaceableService {

    @Override
    public String value() {
        return "original";
    }
}
