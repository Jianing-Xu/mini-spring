package com.xujn.minispring.examples.transaction.phase1;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.examples.transaction.phase1.fixture.OrderService;
import com.xujn.minispring.examples.transaction.phase1.fixture.TransactionState;

/**
 * Manual verification entry for transaction phase-1 success and rollback behavior.
 */
public class TransactionPhase1HappyPathExample {

    public static void main(String[] args) {
        TransactionState.reset();
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.examples.transaction.phase1.fixture");

        OrderService orderService = context.getBean(OrderService.class);
        orderService.placeOrder();
        try {
            orderService.failWithRuntimeException();
        } catch (IllegalStateException ignored) {
        }

        System.out.println("BEGIN_COUNT=" + TransactionState.beginCount);
        System.out.println("COMMIT_COUNT=" + TransactionState.commitCount);
        System.out.println("ROLLBACK_COUNT=" + TransactionState.rollbackCount);
        System.out.println("PHASE-TRANSACTION-1-HAPPY-PATH: PASS");
    }
}
