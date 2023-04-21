项目中用到的一些插件，方便开发、提高效率

1. AOP插件
   AspectJ在安卓中应用，使安卓中也玩Spring中的AOP。对于字节码插桩、性能检测都非常有用。支持增量编译，编译速度很快。
   假如要统计所有Activity的生命周期方法执行耗时？仅需要编写一个切面类即可，原来的Activity无需任何变动。

   - [使用指南](./plugin/aspectj)
   - [示例代码-应用](./example/aspectj-app)
   - [示例代码-模块](./example/aspectj-library)

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

2. 动态权限申请插件
   使用一个注解完成动态权限的申请，再也不用写一堆的权限申请重复性代码了。

   - [使用指南](./library/easy-permission)
   - [示例代码](./example/easy-permission-app)

```java
public class MainActivity {

    @Permission(Manifest.permission.CAMERA)
    private void openCamera() {

    }
}
```

3. APK加固插件
   编译APK后使用腾讯乐固自动完成加固，输出加固后的APK文件。整个过程仅需要执行一个gradle task。

   - [使用指南](./plugin/tencent-legu)
   - [示例代码](./example/legu-app)

```shell
gradlew app:assembleReleaseLegu
```