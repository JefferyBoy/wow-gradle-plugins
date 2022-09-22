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
    fun onCreate() {
    }

    @Around("onCreate()")
    @Throws(Throwable::class)
    fun onCreateAround(joinPoint: ProceedingJoinPoint) {
        println("before method: " + joinPoint.signature)
        joinPoint.proceed(joinPoint.args)
        println("after method: " + joinPoint.signature)
    }

    companion object {
        fun aspectOf(): JavaActivityAspect {
            return JavaActivityAspect()
        }
    }
}
