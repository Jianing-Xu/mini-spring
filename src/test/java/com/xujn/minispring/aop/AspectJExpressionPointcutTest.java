package com.xujn.minispring.aop;

import com.xujn.minispring.aop.aspectj.AspectJExpressionPointcut;
import com.xujn.minispring.test.phase2.aop.service.TestServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AspectJExpressionPointcutTest {

    @Test
    void shouldMatchPackageAndMethodPatterns() throws Exception {
        AspectJExpressionPointcut pointcut =
                new AspectJExpressionPointcut("execution(* com.xujn.minispring.test.phase2.aop.service.*.*(..))");
        Method method = TestServiceImpl.class.getMethod("doSomething");

        assertTrue(pointcut.getClassFilter().matches(TestServiceImpl.class));
        assertTrue(pointcut.getMethodMatcher().matches(method, TestServiceImpl.class));
    }

    @Test
    void shouldMatchAnyClassWithSpecificMethodName() throws Exception {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut("execution(* *.doSomething(..))");
        Method method = TestServiceImpl.class.getMethod("doSomething");

        assertTrue(pointcut.getMethodMatcher().matches(method, TestServiceImpl.class));
    }

    @Test
    void shouldRejectNonMatchingPackage() {
        AspectJExpressionPointcut pointcut =
                new AspectJExpressionPointcut("execution(* com.xujn.minispring.test.phase2.aop.other.*.*(..))");

        assertFalse(pointcut.getClassFilter().matches(TestServiceImpl.class));
    }
}
