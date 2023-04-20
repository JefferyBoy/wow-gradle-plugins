package top.amake.aspectj;

/**
 * @author mxlei
 * @date 2022/9/21
 */
//@Aspect
//public class JavaActivityAspect {
//    public static JavaActivityAspect aspectOf() {
//        return new JavaActivityAspect();
//    }
//
//    @Pointcut("execution(* top.amake.aspectj.*Activity.*(..))")
//    public void pointcut1() {
//    }
//
//    @Around("pointcut1()")
//    public void onCreateAround(final ProceedingJoinPoint joinPoint) throws Throwable {
//        System.out.println("java aspect before method: " + joinPoint.getSignature());
//        joinPoint.proceed(joinPoint.getArgs());
//        System.out.println("java aspect after method: " + joinPoint.getSignature());
//    }
//}
