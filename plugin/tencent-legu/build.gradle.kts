plugins {
    id("java")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish").version("1.2.0")
}

group = "top.amake"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compileOnly("com.android.tools.build:gradle:7.0.2")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.tencentcloudapi:tencentcloud-sdk-java:4.0.11")
    implementation("com.qcloud:cos_api:5.6.57")
    implementation("com.tencent.cloud:cos-sts-java:3.0.8")
}
tasks.withType(Javadoc::class.java).all {
    isFailOnError = false
}

gradlePlugin {
    website.set("https://github.com/JefferyBoy/wow-gradle-plugins/blob/master/plugin/tencent-legu/README.md")
    vcsUrl.set("https://github.com/JefferyBoy/wow-gradle-plugins.git")
    plugins {
        create("legu") {
            id = "top.amake.legu"
            implementationClass = "top.amake.legu.LeguPlugin"
            displayName = "Android apk protect by tencent legu"
            description = "Android apk protect by tencent legu"
            tags.set(listOf("apk", "jiagu", "protect", "safety"))
        }
    }
}
