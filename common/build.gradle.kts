plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.stone.common"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 28
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    api(libs.arouter.api)
    implementation(libs.androidx.core.ktx)
}
