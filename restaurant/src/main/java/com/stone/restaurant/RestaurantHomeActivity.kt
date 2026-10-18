package com.stone.restaurant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.ModuleEntry
import com.stone.common.RouterPath
import com.stone.common.bindModuleHome

@Route(path = RouterPath.RESTAURANT_MAIN)
class RestaurantHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindModuleHome(
            title = "餐厅管理",
            kicker = "业务模块",
            items = listOf(
                ModuleEntry("餐厅演示", clazz = RestaurantActivity::class.java, subtitle = "占位，不实现业务"),
            ),
        )
    }
}
