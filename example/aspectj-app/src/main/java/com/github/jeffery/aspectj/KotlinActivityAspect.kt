package com.github.jeffery.aspectj

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 * @author mxlei
 * @date   2022/9/22
 */
@Aspect
class KotlinActivityAspect {
    @Pointcut("execution(* com.github.jeffery.aspectj.*Activity.*(..))")
    fun pointcut1() {
    }

    @Pointcut("execution(* com.github.jeffery.aspectj.ui.*Activity.*(..))")
    fun pointcut2() {
    }

    @Around("pointcut1() || pointcut2()")
    @Throws(Throwable::class)
    fun onCreateAround(joinPoint: ProceedingJoinPoint) {
        println("kotlin aspect before method: " + joinPoint.signature)
        joinPoint.proceed(joinPoint.args)
        println("kotlin aspect after method: " + joinPoint.signature)
    }

    companion object {
        @JvmStatic
        fun aspectOf(): KotlinActivityAspect {
            return KotlinActivityAspect()
        }
    }
}
