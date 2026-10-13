package com.stone.interview.app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.stone.common.ModuleEntry
import com.stone.common.RouterPath
import com.stone.common.bindModuleHome

class ShellActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindModuleHome(
            title = "Interview",
            kicker = "组件化入口",
            items = listOf(
                ModuleEntry(
                    "Interview 演示",
                    subtitle = "View · 架构 · 网络",
                    route = RouterPath.MYDEMO_MAIN,
                ),
                ModuleEntry(
                    "餐厅管理",
                    subtitle = "业务 module 占位",
                    route = RouterPath.RESTAURANT_MAIN,
                ),
                ModuleEntry(
                    "Kotlin",
                    subtitle = "协程 · Flow · Compose",
                    route = RouterPath.KOTLIN_MAIN,
                ),
            ),
        )
    }
}
