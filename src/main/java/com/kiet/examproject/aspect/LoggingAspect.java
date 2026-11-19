package com.kiet.examproject.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * =============================================================================
 * ASPECT-ORIENTED PROGRAMMING (AOP) LAYER: LoggingAspect
 * =============================================================================
 * Course: CS336B - Object Oriented Prog. Using Java (ODD SEM-2026-27)
 * Core Concepts & Viva Explanations:
 *
 * 1. What is Aspect-Oriented Programming (AOP)?
 *    AOP is a programming paradigm that increases modularity by allowing the
 *    separation of cross-cutting concerns (logging, security, transaction management)
 *    from the core business logic.
 *
 * 2. Key AOP Terminology:
 *    - Aspect: A module containing code for a cross-cutting concern (this class).
 *    - Join Point: A candidate point in the execution of a program (e.g. method execution).
 *    - Pointcut: A predicate expression that selects specific Join Points.
 *    - Advice: The action taken by an Aspect at a Join Point (@Before, @After, @AfterReturning, etc.).
 *    - Target Object: The object being advised (StudentController, StudentService).
 *    - Weaving: The process of linking aspects with target objects to create advised proxy objects.
 *
 * 3. Advices Demonstrated:
 *    - @Before: Runs before the matched target method begins execution.
 *    - @After: Runs after the matched target method completes (regardless of success or failure).
 *    - @AfterReturning: Runs when the method successfully returns a value.
 *    - @AfterThrowing: Runs if the method throws an unhandled exception.
 * =============================================================================
 */
@Aspect
@Component
public class LoggingAspect {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // =========================================================================
    // POINTCUT EXPRESSIONS
    // =========================================================================

    /**
     * Pointcut matching all methods in the Controller layer.
     * Expression: execution(return_type package.class.method(parameters))
     */
    @Pointcut("execution(* com.kiet.examproject.controller..*.*(..))")
    public void controllerMethods() {}

    /**
     * Pointcut matching all methods in the Service layer.
     */
    @Pointcut("execution(* com.kiet.examproject.service..*.*(..))")
    public void serviceMethods() {}

    /**
     * Combined Pointcut for all Controller and Service methods.
     */
    @Pointcut("controllerMethods() || serviceMethods()")
    public void applicationMethods() {}

    // =========================================================================
    // ADVICES
    // =========================================================================

    /**
     * @Before Advice:
     * Intercepts method execution before it begins and logs method signature and arguments.
     *
     * @param joinPoint Metadata about the intercepted method
     */
    @Before("applicationMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        System.out.println(String.format(
                "[AOP @Before] [%s] -> ENTER: %s.%s() | Args: %s",
                timestamp, className, methodName, Arrays.toString(args)
        ));
    }

    /**
     * @After Advice:
     * Executes after the method completes, acting like a finally block.
     *
     * @param joinPoint Metadata about the intercepted method
     */
    @After("applicationMethods()")
    public void logAfterMethod(JoinPoint joinPoint) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        System.out.println(String.format(
                "[AOP @After]  [%s] <- EXIT:  %s.%s() completed execution",
                timestamp, className, methodName
        ));
    }

    /**
     * @AfterReturning Advice:
     * Executes only when a method completes normally and returns a value.
     *
     * @param joinPoint Metadata about the intercepted method
     * @param result Value returned by the target method
     */
    @AfterReturning(pointcut = "applicationMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        String resultStr = (result != null) ? result.toString() : "void/null";
        if (resultStr.length() > 120) {
            resultStr = resultStr.substring(0, 117) + "...";
        }

        System.out.println(String.format(
                "[AOP @AfterReturning] [%s] -> RETURN: %s.%s() => Result: %s",
                timestamp, className, methodName, resultStr
        ));
    }

    /**
     * @AfterThrowing Advice:
     * Executes when a method throws an exception.
     *
     * @param joinPoint Metadata about the intercepted method
     * @param error The thrown exception
     */
    @AfterThrowing(pointcut = "applicationMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        System.err.println(String.format(
                "[AOP @AfterThrowing]  [%s] !! ERROR: %s.%s() threw Exception: [%s] %s",
                timestamp, className, methodName, error.getClass().getSimpleName(), error.getMessage()
        ));
    }
}
