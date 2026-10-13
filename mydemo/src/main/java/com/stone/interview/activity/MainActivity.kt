package com.stone.interview.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.ModuleEntry
import com.stone.common.RouterPath
import com.stone.common.bindModuleHome
import com.stone.interview.anr.AnrActivity
import com.stone.interview.binder.BinderActivity
import com.stone.interview.checkcode.Checkcode
import com.stone.interview.dagger.DaggerActivity
import com.stone.interview.databinding.ObservableActivity
import com.stone.interview.foursymbols.FourSymbolsActivity
import com.stone.interview.fragmentlife.PhotoLifecycleActivity
import com.stone.interview.glide.GlideActivity
import com.stone.interview.handler.HandlerActivity
import com.stone.interview.hilt.HiltActivity
import com.stone.interview.hotfix.HotfixActivity
import com.stone.interview.lifecycle.LifecycleDemoActivity
import com.stone.interview.livedata.LivedataActivity
import com.stone.interview.mvi.MviActivity
import com.stone.interview.mvvm.DemoActivity
import com.stone.interview.retrofit.RetrofitActivity
import com.stone.interview.shape.ShapeCanvasActivity

@Route(path = RouterPath.MYDEMO_MAIN)
class MainActivity : AppCompatActivity() {
    companion object {
        private val nativeLoaded: Boolean = try {
            System.loadLibrary("interviewc")
            true
        } catch (t: Throwable) {
            Log.w("MainActivity", "interviewc not loaded", t)
            false
        }
    }

    external fun stringFromJNI(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bindModuleHome(
            title = "Interview 演示",
            kicker = "MyDemo",
            items = listOf(
                ModuleEntry("MVVM", clazz = DemoActivity::class.java, subtitle = "ViewModel"),
                ModuleEntry("验证码", clazz = Checkcode::class.java, subtitle = "倒计时"),
                ModuleEntry("Observable", clazz = ObservableActivity::class.java, subtitle = "DataBinding"),
                ModuleEntry("Lifecycle", clazz = LifecycleDemoActivity::class.java, subtitle = "生命周期"),
                ModuleEntry("LiveData", clazz = LivedataActivity::class.java, subtitle = "观察者"),
                ModuleEntry("Dagger2", clazz = DaggerActivity::class.java, subtitle = "依赖注入"),
                ModuleEntry("Hilt", clazz = HiltActivity::class.java, subtitle = "Hilt 注入"),
                ModuleEntry("MVI", clazz = MviActivity::class.java, subtitle = "单向数据流"),
                ModuleEntry("Binder", clazz = BinderActivity::class.java, subtitle = "AIDL"),
                ModuleEntry("Handler", clazz = HandlerActivity::class.java, subtitle = "消息机制"),
                ModuleEntry("ANR", clazz = AnrActivity::class.java, subtitle = "卡顿演示"),
                ModuleEntry("热修复", clazz = HotfixActivity::class.java, subtitle = "美团式"),
                ModuleEntry("Retrofit", clazz = RetrofitActivity::class.java, subtitle = "enqueue"),
                ModuleEntry("Glide", clazz = GlideActivity::class.java, subtitle = "图片加载"),
                ModuleEntry("四象 Tab", clazz = FourSymbolsActivity::class.java, subtitle = "ViewPager2"),
                ModuleEntry("Fragment 生命周期", clazz = PhotoLifecycleActivity::class.java, subtitle = "hide / show"),
                ModuleEntry("自定义画布", clazz = ShapeCanvasActivity::class.java, subtitle = "拖动缩放"),
            ),
        )

        if (nativeLoaded) {
            Log.v("wyxwyx", stringFromJNI())
        }
    }

}
