package com.xujn.minispring.test.transaction.phase1;

import com.xujn.minispring.context.annotation.Autowired;
import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.tx.annotation.Transactional;

@Component
public class OuterServiceImpl implements OuterService {

    @Autowired
    private InnerService innerService;

    @Override
    @Transactional
    public void outer() {
        innerService.inner();
    }
}
