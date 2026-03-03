package com.xujn.minispring.test.transaction.phase1;

public interface OrderService {

    void placeOrder();

    void failWithRuntimeException();

    void failWithCheckedException() throws Exception;
}
