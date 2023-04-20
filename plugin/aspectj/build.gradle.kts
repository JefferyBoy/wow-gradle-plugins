plugins {
    id("groovy")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish").version("1.2.0")
}

group = "top.amake"
version = "1.0.5"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    website.set("https://github.com/JefferyBoy/wow-gradle-plugins")
    vcsUrl.set("https://github.com/JefferyBoy/wow-gradle-plugins")
    plugins {
        create("aspectj") {
            id = "top.amake.aspectj"
            implementationClass = "top.amake.aspectj.AspectjPlugin"
            displayName = "Android aspectj plugin"
            description =
                "Support eclipse aspectj on android app and library module. Use transform api to improve build performance."
            tags.set(listOf("AspectJ", "AOP", "aspect"))
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.aspectj:aspectjweaver:1.9.19")
    implementation("org.aspectj:aspectjtools:1.9.19")
    compileOnly("com.android.tools.build:gradle:7.0.2")
}