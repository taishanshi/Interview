package com.stone.kotlin

import android.app.Application
import com.stone.common.RouterInit
import com.stone.kotlin.utils.abc

/**
 * 仅独立运行时由 module Manifest 指定。
 * 放在 src/main 里，避免 AGP 9 额外 java.srcDir 编不进 Kotlin 源码。
 */
class KotlinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        println("result 111 ${applicationContext.abc(1,2)}")
        RouterInit.init(this)
    }
}
