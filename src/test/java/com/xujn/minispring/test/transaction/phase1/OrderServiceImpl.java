package com.xujn.minispring.test.transaction.phase1;

import com.xujn.minispring.context.annotation.Component;
import com.xujn.minispring.tx.annotation.Transactional;

@Component
public class OrderServiceImpl implements OrderService {

    @Override
    @Transactional
    public void placeOrder() {
    }

    @Override
    @Transactional
    public void failWithRuntimeException() {
        throw new IllegalStateException("runtime failure");
    }

    @Override
    @Transactional
    public void failWithCheckedException() throws Exception {
        throw new Exception("checked failure");
    }
}
