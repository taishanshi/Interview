package com.stone.interview

import android.app.Application
import com.stone.common.RouterInit
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RouterInit.init(this)
    }
}
