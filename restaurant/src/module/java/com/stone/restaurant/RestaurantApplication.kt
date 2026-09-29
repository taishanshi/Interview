package com.stone.restaurant

import android.app.Application
import com.stone.common.RouterInit

class RestaurantApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RouterInit.init(this)
    }
}
