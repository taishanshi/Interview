plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.legacy.kapt)
    alias(libs.plugins.hilt)
}

val isMyDemoApplication = rootProject.extra["isMyDemoApplication"] as Boolean
val isRestaurantApplication = rootProject.extra["isRestaurantApplication"] as Boolean
val isKotlinApplication = rootProject.extra["isKotlinApplication"] as Boolean

android {
    namespace = "com.stone.interview.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.stone.interview"
        minSdk = 28
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // 业务 module 里用了 DataBinding。合并后的 DataBinderMapperImpl
    // 只在 Application 工程生成，壳不开这个会在运行时 NoClassDefFoundError。
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation(project(":common"))
    // 业务 module 处于 Library 模式时才被壳工程依赖；独立 Application 时不能 implementation 它
    if (!isMyDemoApplication) {
        implementation(project(":mydemo"))
    }
    if (!isRestaurantApplication) {
        implementation(project(":restaurant"))
    }
    if (!isKotlinApplication) {
        implementation(project(":KotlinModule"))
    }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
