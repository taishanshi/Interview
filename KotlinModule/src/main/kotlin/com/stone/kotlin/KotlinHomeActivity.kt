package com.stone.kotlin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.ModuleEntry
import com.stone.common.RouterPath
import com.stone.common.bindModuleHome
import com.stone.kotlin.compose.ComposeAccompanistActivity
import com.stone.kotlin.compose.ComposeBasicsActivity
import com.stone.kotlin.compose.ComposeLaunchedEffectActivity
import com.stone.kotlin.compose.ComposeLiveDataActivity
import com.stone.kotlin.compose.ComposeNavigationActivity
import com.stone.kotlin.compose.ComposeCanvasActivity
import com.stone.kotlin.compose.ComposePhotoGridActivity
import com.stone.kotlin.compose.ComposePlaygroundActivity
import com.stone.kotlin.compose.ComposeScrollConflictActivity
import com.stone.kotlin.compose.ComposeScaffoldActivity
import com.stone.kotlin.compose.ComposeTabScrollActivity
import com.stone.kotlin.compose.ComposeWaterfallActivity
import com.stone.kotlin.flow.FlowActivity
import com.stone.kotlin.flow.KotlinScopeActivity
import com.stone.kotlin.utils.abc

@Route(path = RouterPath.KOTLIN_MAIN)
class KotlinHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        bindModuleHome(
            title = "Kotlin",
            kicker = "Kotlin Module",
            items = listOf(
                ModuleEntry("协程", clazz = KotlinScopeActivity::class.java, subtitle = "lifecycleScope"),
                ModuleEntry("Flow", clazz = FlowActivity::class.java, subtitle = "冷流与热流"),
                ModuleEntry("Compose 基础", clazz = ComposeBasicsActivity::class.java, subtitle = "控件与动画"),
                ModuleEntry("Scaffold", clazz = ComposeScaffoldActivity::class.java, subtitle = "页面骨架"),
                ModuleEntry("瀑布流", clazz = ComposeWaterfallActivity::class.java, subtitle = "自定义 Layout"),
                ModuleEntry("Compose Tab", clazz = ComposeTabScrollActivity::class.java, subtitle = "Pager + 滚动"),
                ModuleEntry("Compose 画布", clazz = ComposeCanvasActivity::class.java, subtitle = "贝塞尔与手势"),
                ModuleEntry("李一桐", clazz = ComposePhotoGridActivity::class.java, subtitle = "轮播 + 2×2"),
                ModuleEntry("Compose 手写", clazz = ComposePlaygroundActivity::class.java, subtitle = "空白练习页"),
                ModuleEntry("Accompanist", clazz = ComposeAccompanistActivity::class.java, subtitle = "权限封装"),
                ModuleEntry("LiveData", clazz = ComposeLiveDataActivity::class.java, subtitle = "delay 模拟请求"),
                ModuleEntry("LaunchedEffect", clazz = ComposeLaunchedEffectActivity::class.java, subtitle = "组合里的协程"),
                ModuleEntry("滑动冲突", clazz = ComposeScrollConflictActivity::class.java, subtitle = "内外拦截"),
                ModuleEntry("Navigation", clazz = ComposeNavigationActivity::class.java, subtitle = "NavHost 回退栈"),
            ),
        )
    }
}
