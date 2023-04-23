[Chinese](./README_CN.md)

Some useful gradle plugin in android project.

1. AOP Plugin
   AspectJ is applied in Android, so that Android can also play AOP in Spring. It is very useful for
   bytecode instrumentation and performance testing. Incremental compilation is supported, and the
   compilation speed is very fast. What if you want to count the execution time of all Activity
   lifecycle methods? Only one aspect class needs to be written, and the original Activity does not
   need any changes.
   - [Guide](./plugin/aspectj)
   - [Demo-app](./example/aspectj-app)
   - [Demo-library](./example/aspectj-library)

```java

@Aspect
public class JavaActivityAspect {
   public static JavaActivityAspect aspectOf() {
      return new JavaActivityAspect();
   }

   @Pointcut("execution(* top.amake.aspectj.*Activity.*(..))")
   public void pointcut1() {
   }

   @Around("pointcut1()")
   public void onCreateAround(final ProceedingJoinPoint joinPoint) throws Throwable {
      long start = System.currentTimeMillis();
      joinPoint.proceed(joinPoint.getArgs());
      long end = System.currentTimeMillis();
      System.out.println("方法：" + joinPoint.getSignature().getName() + " 耗时：" + (end - start));
   }
}
```

2. Dynamic permission plugin
   Use one annotation to complete the application of dynamic permissions, and no longer need to
   write a bunch of repetitive codes for permission applications.

   - [Guide](./library/easy-permission)
   - [Demo](./example/easy-permission-app)

```java
public class MainActivity {

    @Permission(Manifest.permission.CAMERA)
    private void openCamera() {

    }
}
```

3. APK protect plugin
   After compiling the APK, use Tencent Legu to automatically complete the hardening, and output the
   hardened APK file. The whole process only needs to execute a gradle task.

   - [Guide](./plugin/tencent-legu)
   - [Demo](./example/legu-app)

```shell
gradlew app:assembleReleaseLegu
```