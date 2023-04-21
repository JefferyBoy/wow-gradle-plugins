# android-aspectj-gradle-plugin

[![](https://www.jitpack.io/v/JefferyBoy/wow-gradle-plugins.svg)](https://www.jitpack.io/#JefferyBoy/wow-gradle-plugins)

在安卓中使用aspectj。全部功能都是aspectj官方的，这个插件仅仅是做aspectj和android gradle plugin的连接桥梁作用。
在安卓项目编译完成后执行aspectj进行class字节码修改。

1. 支持java和kotlin
2. 支持app和library的module
3. 支持增量编译，提供编译速度

## 1.引入插件

在build.gradle中加入

```gradle
plugins {
    id("top.amake.aspectj").version("1.0.5")
}
```

demo代码请查看
[example-aspectj-app](https://github.com/JefferyBoy/wow-gradle-plugins/tree/master/example/aspectj-app)

## 2.编写切面

```java

@Aspect
public class ActivityAspect {
    public static ActivityAspect aspectOf() {
        return new ActivityAspect();
    }

    @Pointcut("execution(* top.amake.aspectj.*Activity.*(..))")
    public void onActivityAny() {
    }

    @Around("onActivityAny()")
    public void onCreateAround(final ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("before method: " + joinPoint.getSignature());
        joinPoint.proceed(joinPoint.getArgs());
        System.out.println("after method: " + joinPoint.getSignature());
    }
}
```

## 3. 切点代码

这里以Activity中的代码为例

```java
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    private void test(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        test("hello");
    }
}
```

## 4. 编译运行app

运行app后点击测试按钮得到如下输出

![](https://fastly.jsdelivr.net/gh/JefferyBoy/pictures@master/2022/16637298527541663729851839.png)

