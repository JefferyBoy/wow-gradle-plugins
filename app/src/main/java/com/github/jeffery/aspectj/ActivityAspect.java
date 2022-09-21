package com.github.jeffery.aspectj;

import android.os.Bundle;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author mxlei
 * @date 2022/9/21
 */
@Aspect
public class ActivityAspect {
    public static ActivityAspect aspectOf() {
        return new ActivityAspect();
    }

    @Pointcut("*android.app.Activity+ * onCreate(android.os.Bundle)")
    public void onCreate(Bundle bundle) {

    }

    @Around("onCreate(bundle)")
    public void onCreateAround(final ProceedingJoinPoint joinPoint, Bundle bundle) throws Throwable {
        System.out.println("onCreate before");
        joinPoint.proceed(new Object[] {bundle});
        System.out.println("onCreate after");
    }
}
