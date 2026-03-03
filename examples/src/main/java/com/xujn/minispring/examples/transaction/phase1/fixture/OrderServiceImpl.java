package com.xujn.minispring.examples.transaction.phase1.fixture;

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
}
