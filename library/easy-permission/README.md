# android动态权限申请

[![](https://jitpack.io/v/JefferyBoy/dpermission.svg)](https://jitpack.io/#JefferyBoy/dpermission)

使用aspectj编译期class字节码插入实现动态权限申请

使用方式

1. 在项目根目录的build.gradle中加入

```gradle
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.github.JefferyBoy:android-aspectj-gradle-plugin:1.0.3'
    }
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

2. 在需要使用的模块中build.gradle中加入aspectj支持

```gradle
apply plugin: 'android.aspectj'

dependencies {
    implementation 'com.github.JefferyBoy:android-dynamic-permissions:0.1.3'
}
```

3. 项目中使用

```java

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });
    }

    /**
     * 使用@Permission注解需要申请动态权限后再执行的方法
     * */
    @Permission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void requestStoragePermission() {
        // 权限申请成功后才会执行到这里
        Log.d(TAG, "requestStoragePermission ok");
    }

    /**
     * 申请权限被拒绝后执行的方法
     * 参数可以为List\<PermisssionResult\> 或者 无参数 
     * */
    @PermissionDenied()
    void onPermissionDenied(List<PermissionResult> results) {
        // 权限申请被拒绝
        Log.d(TAG, "requestPermissionDenied");
        for (PermissionResult result : results) {
            System.out.println(result);
        }
    }
}

```