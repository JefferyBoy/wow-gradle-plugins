package com.github.jeffery.aspectj

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 * @author jeffery
 * @date   4/18/23
 */
@Aspect
class TestAspect2 {

    //    @Pointcut("execution(* com.github.jeffery.aspectj.*Activity.*(..))")
    @Pointcut("execution(* com.github.jeffery.aspectj.AA.*(..))")
    fun testPoint() {

    }

    @Around("testPoint()")
    fun testAround(point: ProceedingJoinPoint) {
        println("before")
        point.proceed()
        println("after")
    }

    companion object {
        @JvmStatic
        fun aspectOf(): TestAspect2 {
            return TestAspect2()
        }
    }
}