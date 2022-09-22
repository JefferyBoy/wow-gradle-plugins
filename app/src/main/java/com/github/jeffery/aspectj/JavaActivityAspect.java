package com.github.jeffery.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author mxlei
 * @date 2022/9/21
 */
@Aspect
public class JavaActivityAspect {
    public static JavaActivityAspect aspectOf() {
        return new JavaActivityAspect();
    }

    @Pointcut("execution(* com.github.jeffery.aspectj.*Activity.*(..))")
    public void onCreate() {
    }

    @Around("onCreate()")
    public void onCreateAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before method: " + joinPoint.getSignature());
        joinPoint.proceed(joinPoint.getArgs());
        System.out.println("after method: " + joinPoint.getSignature());
    }
}
