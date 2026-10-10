package com.stone.interview.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.stone.common.RouterPath
import com.stone.interview.checkcode.Checkcode
import com.stone.interview.R
import com.stone.interview.anr.AnrActivity
import com.stone.interview.binder.BinderActivity
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
    private val demos: List<DemoEntry>
        get() {
            val listOf = listOf(
                DemoEntry("MVVM", DemoActivity::class.java),
                DemoEntry("验证码", Checkcode::class.java),
                DemoEntry("Observable", ObservableActivity::class.java),
                DemoEntry("Lifecycle", LifecycleDemoActivity::class.java),
                DemoEntry("LiveData", LivedataActivity::class.java),
                DemoEntry("Dagger2", DaggerActivity::class.java),
                DemoEntry("Hilt", HiltActivity::class.java),
                DemoEntry("MVI", MviActivity::class.java),
                DemoEntry("Binder", BinderActivity::class.java),
                DemoEntry("Handler", HandlerActivity::class.java),
                DemoEntry("ANR", AnrActivity::class.java),
                DemoEntry("热修复", HotfixActivity::class.java),
                DemoEntry("Retrofit", RetrofitActivity::class.java),
                DemoEntry("Glide", GlideActivity::class.java),
                DemoEntry("四象 Tab", FourSymbolsActivity::class.java),
                DemoEntry("Fragment 生命周期", PhotoLifecycleActivity::class.java),
                DemoEntry("餐厅管理", route = RouterPath.RESTAURANT_MAIN),
            )
            return listOf
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (nativeLoaded) {
            Log.v("wyxwyx", stringFromJNI())
        }
        initView()
        testWork()
    }

    fun testWork() {
        Log.v("wyxwyx", "classloader = $classLoader")
        Log.v("wyxwyx", "classloader2 = ${classLoader.parent}")
        Log.v("wyxwyx", "classloader3 = ${classLoader.parent.parent}")
        //MemoryLearn().start()
    }

    fun initView() {
        val root = findViewById<View>(R.id.main)
        val padL = root.paddingLeft
        val padT = root.paddingTop
        val padR = root.paddingRight
        val padB = root.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + padL,
                systemBars.top + padT,
                systemBars.right + padR,
                systemBars.bottom + padB
            )
            insets
        }

        val grid = findViewById<GridView>(R.id.gridDemos)
        grid.adapter = DemoGridAdapter(demos)
        grid.setOnItemClickListener { _, _, position, _ ->
            val item = demos[position]
            if (item.route != null) {
                ARouter.getInstance().build(item.route).navigation()
            } else {
                startActivity(Intent(this, item.target))
            }
        }
    }
}

private data class DemoEntry(
    val title: String,
    val target: Class<out Activity>? = null,
    val route: String? = null,
)

private class DemoGridAdapter(
    private val items: List<DemoEntry>
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): DemoEntry = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_demo_grid, parent, false)
        view.findViewById<TextView>(R.id.tvItem).text = items[position].title
        return view
    }
}
