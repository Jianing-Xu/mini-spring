package com.xujn.minispring.context;

import com.xujn.minispring.context.support.AnnotationConfigApplicationContext;
import com.xujn.minispring.test.transaction.phase1.ClassLevelService;
import com.xujn.minispring.test.transaction.phase1.NoInterfaceTransactionalBean;
import com.xujn.minispring.test.transaction.phase1.OrderService;
import com.xujn.minispring.test.transaction.phase1.OuterService;
import com.xujn.minispring.test.transaction.phase1.PrecedenceService;
import com.xujn.minispring.test.transaction.phase1.RecordingTransactionManager;
import com.xujn.minispring.test.transaction.phase1.TransactionState;
import com.xujn.minispring.test.transaction.phase1.UserService;
import com.xujn.minispring.tx.interceptor.AnnotationTransactionAttributeSource;
import com.xujn.minispring.tx.support.TransactionSynchronizationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionPhase1AcceptanceTest {

    @BeforeEach
    void setUp() {
        TransactionState.reset();
        TransactionSynchronizationManager.clear();
    }

    @Test
    void shouldCommitOnSuccessfulTransactionalInvocation() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        context.getBean(OrderService.class).placeOrder();

        assertEquals(1, TransactionState.beginCount);
        assertEquals(1, TransactionState.commitCount);
        assertEquals(0, TransactionState.rollbackCount);
    }

    @Test
    void shouldRollbackOnRuntimeException() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        assertThrows(IllegalStateException.class, () -> context.getBean(OrderService.class).failWithRuntimeException());

        assertEquals(1, TransactionState.beginCount);
        assertEquals(0, TransactionState.commitCount);
        assertEquals(1, TransactionState.rollbackCount);
    }

    @Test
    void shouldCommitOnCheckedExceptionByDefault() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        assertThrows(Exception.class, () -> context.getBean(OrderService.class).failWithCheckedException());

        assertEquals(1, TransactionState.beginCount);
        assertEquals(1, TransactionState.commitCount);
        assertEquals(0, TransactionState.rollbackCount);
    }

    @Test
    void shouldPreferMethodLevelAttributeOverClassLevelMetadata() throws NoSuchMethodException {
        AnnotationTransactionAttributeSource source = new AnnotationTransactionAttributeSource();

        assertEquals("method", source.getTransactionAttribute(
                PrecedenceService.class.getMethod("work"),
                com.xujn.minispring.test.transaction.phase1.PrecedenceServiceImpl.class
        ).getSource());
    }

    @Test
    void shouldApplyClassLevelTransactionalMetadata() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        context.getBean(ClassLevelService.class).work();

        assertEquals(1, TransactionState.beginCount);
        assertEquals(1, TransactionState.commitCount);
        assertEquals(0, TransactionState.rollbackCount);
    }

    @Test
    void shouldJoinExistingRequiredTransactionAndCommitOnce() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        context.getBean(OuterService.class).outer();

        assertEquals(1, TransactionState.beginCount);
        assertEquals(1, TransactionState.commitCount);
        assertEquals(0, TransactionState.rollbackCount);
    }

    @Test
    void shouldClearThreadBoundResourcesAfterCompletion() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");
        RecordingTransactionManager transactionManager = context.getBean(RecordingTransactionManager.class);

        context.getBean(OrderService.class).placeOrder();

        assertFalse(TransactionSynchronizationManager.hasResource(transactionManager));
    }

    @Test
    void shouldNotProxyTransactionalBeanWithoutInterfaces() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        NoInterfaceTransactionalBean bean = context.getBean(NoInterfaceTransactionalBean.class);
        bean.work();

        assertFalse(Proxy.isProxyClass(bean.getClass()));
        assertEquals(0, TransactionState.beginCount);
    }

    @Test
    void shouldNotCreateNewTransactionForSelfInvocation() {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext("com.xujn.minispring.test.transaction.phase1");

        UserService userService = context.getBean(UserService.class);
        userService.outer();

        assertEquals(0, TransactionState.beginCount);
    }
}
