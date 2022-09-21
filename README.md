# android-aspectj-gradle-plugin

在安卓中使用aspectj。全部功能都是aspectj官方的，这个插件仅仅是做aspectj和android gradle plugin的连接桥梁作用。
在安卓项目编译完成后执行aspectj进行class字节码修改。

使用方法

## 1.引入插件包

在项目的根目录build.gradle中加入

```gradle
buildscript {
    repositories {
        // 加入jitpack代码仓库
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        // 加入插件包
        classpath 'com.github.JefferyBoy:android-aspectj-gradle-plugin:1.0.1'
    }
}

```

## 2.使用插件

在需要使用aspect的模块build.gradle中加入

```gradle
apply plugin: 'android.aspectj'
dependencies {
    // aspectj运行时库
    implementation 'org.aspectj:aspectjrt:1.9.9.1'
}
```

## 3.编写aop切面代码

查看demo中的代码

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

```java

@Aspect
public class ActivityAspect {
    public static ActivityAspect aspectOf() {
        return new ActivityAspect();
    }

    /**
     * 包com.github.jeffery.aspectj下的所有Activity的所有方法
     * */
    @Pointcut("execution(* com.github.jeffery.aspectj.*Activity.*(..))")
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

## 4. 编译运行app

运行app后点击测试按钮得到如下输出

![](https://fastly.jsdelivr.net/gh/JefferyBoy/pictures@master/2022/16637298527541663729851839.png)

