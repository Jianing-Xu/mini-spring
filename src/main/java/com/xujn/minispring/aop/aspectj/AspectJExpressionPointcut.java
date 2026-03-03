package com.xujn.minispring.aop.aspectj;

import com.xujn.minispring.aop.ClassFilter;
import com.xujn.minispring.aop.MethodMatcher;
import com.xujn.minispring.aop.Pointcut;
import com.xujn.minispring.exception.BeansException;

import java.lang.reflect.Method;

/**
 * execution() subset pointcut implementation for package and method name matching.
 * Constraint: supports patterns like package.*.*(..), package..*.*(..), and *.method(..).
 * Thread-safety: immutable after construction.
 */
public class AspectJExpressionPointcut implements Pointcut, ClassFilter, MethodMatcher {

    private final String expression;
    private final String classPattern;
    private final String methodPattern;

    public AspectJExpressionPointcut(String expression) {
        this.expression = expression;
        String[] parsed = parseExpression(expression);
        this.classPattern = parsed[0];
        this.methodPattern = parsed[1];
    }

    @Override
    public ClassFilter getClassFilter() {
        return this;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    @Override
    public boolean matches(Class<?> clazz) {
        String className = clazz.getName();
        String packageName = clazz.getPackageName();
        if ("*".equals(classPattern)) {
            return true;
        }
        if (classPattern.endsWith("..*")) {
            String prefix = classPattern.substring(0, classPattern.length() - 3);
            return packageName.equals(prefix) || packageName.startsWith(prefix + ".");
        }
        if (classPattern.endsWith(".*")) {
            String exactPackage = classPattern.substring(0, classPattern.length() - 2);
            return packageName.equals(exactPackage);
        }
        if (classPattern.contains("*")) {
            String regex = classPattern.replace(".", "\\.").replace("*", ".*");
            return className.matches(regex);
        }
        return className.equals(classPattern);
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        return matches(targetClass) && ("*".equals(methodPattern) || method.getName().equals(methodPattern));
    }

    @Override
    public String toString() {
        return expression;
    }

    private String[] parseExpression(String expression) {
        if (expression == null || !expression.startsWith("execution(") || !expression.endsWith(")")) {
            throw new BeansException("Unsupported pointcut expression '" + expression + "'");
        }
        String body = expression.substring("execution(".length(), expression.length() - 1).trim();
        int firstSpace = body.indexOf(' ');
        if (firstSpace < 0) {
            throw new BeansException("Unsupported pointcut expression '" + expression + "'");
        }
        String signature = body.substring(firstSpace + 1).trim();
        if (!signature.endsWith("(..)")) {
            throw new BeansException("Unsupported pointcut expression '" + expression + "'");
        }
        String withoutArgs = signature.substring(0, signature.length() - 4);
        int lastDot = withoutArgs.lastIndexOf('.');
        if (lastDot < 0) {
            throw new BeansException("Unsupported pointcut expression '" + expression + "'");
        }
        return new String[]{withoutArgs.substring(0, lastDot), withoutArgs.substring(lastDot + 1)};
    }
}
