package com.stone.kotlin

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.stone.common.RouterPath
import kotlinx.coroutines.launch

/**
 * 冷流在按钮里 collect，点一次跑一遍。
 * StateFlow / SharedFlow 用 repeatOnLifecycle 收集，页面不可见时取消，回来再订。
 */
@Route(path = RouterPath.KOTLIN_FLOW)
class FlowActivity : AppCompatActivity() {

    private val viewModel: FlowViewModel by viewModels()
    private val lines = ArrayDeque<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.count.collect { value ->
                    findViewById<TextView>(R.id.tvState).text =
                        "StateFlow 当前值 $value（不用点击，订阅就能拿到）"
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    append("SharedFlow 收到 $event")
                }
            }
        }

        findViewById<Button>(R.id.btnCold).setOnClickListener {
            lifecycleScope.launch {
                append("开始 collect，冷流这才执行")
                filteredNumbers().collect { line ->
                    append("$line；收集线程 ${Thread.currentThread().name}")
                }
                append("这次 collect 结束")
            }
        }
        findViewById<Button>(R.id.btnError).setOnClickListener {
            lifecycleScope.launch {
                failingFlow().collect { line -> append(line) }
            }
        }
        findViewById<Button>(R.id.btnState).setOnClickListener {
            viewModel.increment()
        }
        findViewById<Button>(R.id.btnShared).setOnClickListener {
            viewModel.sendEvent()
        }
    }

    private fun append(line: String) {
        if (isDestroyed) return
        lines.addLast(line)
        while (lines.size > 16) {
            lines.removeFirst()
        }
        findViewById<TextView>(R.id.tvLog).text = lines.joinToString("\n")
    }
}
