package top.amake.aspectj

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

/**
 * @author jeffery
 * @date   4/18/23
 */
@Aspect
class MyAspect {


    @Pointcut("execution(* com.github.jeffery.library.Test*.*(..))")
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
        fun aspectOf(): MyAspect {
            return MyAspect()
        }
    }
}