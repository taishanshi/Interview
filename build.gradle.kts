// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.legacy.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.hotfix) apply false
}

// 组件化开关：isMyDemoApplication / isRestaurantApplication
apply(from = "config.gradle")

tasks.register<GradleBuild>("uploadArchives") {
    group = "upload"
    description = "发布 javassist 插件到本地 Maven（javassist/repo）"
    dir = file("javassist")
    tasks = listOf("uploadArchives")
}