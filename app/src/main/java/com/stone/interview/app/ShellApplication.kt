package com.stone.interview.app

import android.app.Application
import com.stone.common.RouterInit
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShellApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RouterInit.init(this)
    }
}
