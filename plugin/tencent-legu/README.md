# 安卓应用加固gradle插件

运行gradle任务，由gradle插件完成应用的加固过程

使用腾讯应用加固服务，需要配置腾讯云的密钥、对象存储库

1. 应用插件 在根目录的build.gradle加入

```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
    dependencies {
        classpath 'com.github.JefferyBoy:android-apk-protect-gradle-plugin:1.0.1'
    }
}
```

在应用app的build.gradle加入

```groovy
apply plugin: 'android.apk.protect'

Properties localProperties = new Properties()
localProperties.load(new FileInputStream(new File(project.rootDir, "local.properties")));
// apk加固配置，腾讯云对象存储用来上传apk
// 加固完成后自动下载apk并签名，打印出加固后的apk文件路径
// release版本请配置好singingConfig签名
apkprotect {
    tencentCloudSecretId = localProperties.get("tencentCloudSecretId")
    tencentCloudSecretKey = localProperties.get("tencentCloudSecretKey")
    tencentCloudCosBucket = localProperties.get("tencentCloudCosBucket")
    tencentCloudCosRegion = localProperties.get("tencentCloudCosRegion")
    tencentCloudCosDeleteFileAfterTask = true
}
```

local.gradle中配置腾讯云的密钥

```properties
tencentCloudSecretId=
tencentCloudSecretKey=
tencentCloudCosBucket=
tencentCloudCosRegion=
```

2. 执行加固任务

```shell
# 任务名称根据app配置的编译编译variant而改变
./gradlew app:protect-assembleRelease
```

执行过程日志如下

```text
> Task :app:protect-assembleRelease
Prepare protect apk /media/mxlei/data/workspace/android/android-apk-protect/app/build/outputs/apk/release/app-release.apk
Uploading file
Upload success
Create apk protect task
Processing protect apk, this will take several minutes.
please wait 5 seconds
please wait 15 seconds
please wait 25 seconds
please wait 35 seconds
please wait 45 seconds
please wait 55 seconds
Protect success
Download file
Download progress 1.0%
Download progress 19.1%
Download progress 37.3%
Download progress 55.4%
Download progress 73.6%
Download progress 91.8%
Download progress 99.8%
Download complete
Protect success
/media/mxlei/data/workspace/android/android-apk-protect/app/build/outputs/apk/release/app-release-protected.apk
Apk Signature success
/media/mxlei/data/workspace/android/android-apk-protect/app/build/outputs/apk/release/app-release-protected-signed.apk

BUILD SUCCESSFUL in 1m 36s
38 actionable tasks: 38 executed
```