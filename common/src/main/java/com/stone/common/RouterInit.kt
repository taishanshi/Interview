package com.stone.common

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

object RouterInit {
    fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            ARouter.openLog()
            ARouter.openDebug()
        }
        ARouter.init(application)
    }
}
