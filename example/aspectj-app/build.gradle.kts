plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("top.amake.aspectj")
}

android {
    compileSdk = 31
    namespace = "top.amake.aspectj"

    defaultConfig {
        applicationId = "com.github.jeffery.aspectj"
        minSdk = 16
        targetSdk = 31
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

    flavorDimensions.add("type")

    productFlavors {
        create("free") {
            minSdk = 23
        }
        create("paid") {
            minSdk = 23
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("org.aspectj:aspectjrt:1.9.19")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}