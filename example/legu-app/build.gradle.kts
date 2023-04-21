plugins {
    id("com.android.application")
    id("top.amake.legu")
}

android {
    namespace = "top.amake.legu"
    compileSdk = 33

    defaultConfig {
        applicationId = "top.amake.legu"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

// 腾讯乐固加固APK
legu {
    // 腾讯云secretId
    tencentCloudSecretId = ""
    // 腾讯云secretKey
    tencentCloudSecretKey = ""
    // 腾讯COS对象存储bucket名称
    tencentCloudCosBucket = ""
    // 腾讯云COS对象存储的区域名称
    tencentCloudCosRegion = ""
    // 下载后删除COS中的文件
    isTencentCloudCosDeleteFileAfterTask = true
}