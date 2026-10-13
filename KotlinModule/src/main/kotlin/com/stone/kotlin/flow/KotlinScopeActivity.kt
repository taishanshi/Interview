package com.stone.kotlin.flow

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import com.stone.kotlin.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * lifecycleScope：协程挂在 Activity 上，onDestroy 时取消。
 * 切到后台只是 onStop，这个 scope 还活着，所以不要在里面改界面。
 *
 * repeatOnLifecycle(STARTED)：包住的代码只在前台跑。
 * onStop 会取消里面的协程，回到前台再重新执行。收集 Flow 更新界面用这个。
 *
 * viewModelScope：见 ScopeViewModel。
 */
@Route(path = RouterPath.KOTLIN_SCOPE)
class KotlinScopeActivity : AppCompatActivity() {

    private val viewModel: ScopeViewModel by viewModels()
    private var lifecycleCount = 0
    private var repeatCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_scope)
        showCounts()
        viewModel.append("onCreate")

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.log.collect { text ->
                    findViewById<TextView>(R.id.tvLog).text = text
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tick.collect { value ->
                    findViewById<TextView>(R.id.tvViewModel).text =
                        "viewModelScope：$value（切后台继续涨，旋转屏幕也还在）"
                }
            }
        }

        val lifecycleJob = lifecycleScope.launch {
            while (isActive) {
                delay(1000)
                lifecycleCount++
                showCounts()
            }
        }
        lifecycleJob.invokeOnCompletion { cause ->
            Log.d(TAG, "lifecycleScope 协程结束: ${cause?.javaClass?.simpleName ?: "正常结束"}")
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    viewModel.append("repeatOnLifecycle 进入，只在前台计数")
                    while (isActive) {
                        delay(1000)
                        repeatCount++
                        showCounts()
                    }
                } finally {
                    if (!isDestroyed) {
                        viewModel.append("repeatOnLifecycle 退出，界面不可见时停住")
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.append("onStart")
    }

    override fun onStop() {
        super.onStop()
        viewModel.append("onStop：lifecycleScope 仍在跑")
    }

    override fun onDestroy() {
        viewModel.append("onDestroy：lifecycleScope 被取消")
        super.onDestroy()
    }

    private fun showCounts() {
        if (isDestroyed) return
        findViewById<TextView>(R.id.tvLifecycle).text =
            "lifecycleScope：$lifecycleCount（切后台继续涨，旋转屏幕从 0 开始）"
        findViewById<TextView>(R.id.tvRepeat).text =
            "repeatOnLifecycle：$repeatCount（切后台停住，回来续上；旋转屏幕从 0 开始）"
    }

    private companion object {
        const val TAG = "KotlinScope"
    }
}
