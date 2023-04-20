plugins {
    id("groovy")
    id("java-gradle-plugin")
}

group = "com.github.jeffery"
version = "1.0.5"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    website.set("git@github.com:JefferyBoy/wow-gradle-plugins.git")
    vcsUrl.set("git@github.com:JefferyBoy/wow-gradle-plugins.git")
    plugins {
        create("aspectj") {
            id = "android.build.aspectj"
            implementationClass = "com.github.jeffery.aspectj.AspectjPlugin"
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