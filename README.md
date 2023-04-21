项目中用到的一些插件，方便开发、提高效率

1. AOP插件
   AspectJ在安卓中应用，使安卓中也玩Spring中的AOP。对于字节码插桩、性能检测都非常有用。支持增量编译，编译速度很快。
   假如要统计所有Activity的生命周期方法执行耗时？仅需要编写一个切面类即可，原来的Activity无需任何变动。
2. 动态权限申请插件
   使用一个注解完成动态权限的申请，再也不用写一堆的权限申请重复性代码了。

```java
public class MainActivity {

    @Permission(Manifest.permission.CAMERA)
    private void openCamera() {

    }
}
```

3. APK加固插件
   编译APK后使用腾讯乐固自动完成加固，输出加固后的APK文件。整个过程仅需要执行一个gradle task。