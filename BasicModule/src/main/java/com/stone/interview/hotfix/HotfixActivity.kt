package com.stone.interview.hotfix

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R
import com.stone.interview.hotfix.HotfixManager

class HotfixActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "Hotfix"
        private const val PATCH = "com.stone.interview.hotfix.patch.HotfixTargetPatch"
    }

    private val target = HotfixTarget()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hotfix)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!HotfixManager.hasHook(HotfixTarget::class.java)) {
            appendLog("没有 changeQuickRedirect。先跑 insertHotfixDebug 再安装")
        } else {
            appendLog("已插桩，未加载补丁时走原始编译代码")
        }

        findViewById<Button>(R.id.btnCall).setOnClickListener { refresh("调用") }
        findViewById<Button>(R.id.btnLoad).setOnClickListener {
            try {
                HotfixManager.loadAndApply(HotfixTarget::class.java, PATCH, classLoader)
                appendLog("已反射加载补丁，再点调用看差值")
                refresh("加载后")
            } catch (e: Exception) {
                Log.e(TAG, "load patch", e)
                appendLog("加载失败: ${e.message}")
            }
        }
        findViewById<Button>(R.id.btnUnload).setOnClickListener {
            try {
                HotfixManager.clear(HotfixTarget::class.java)
                appendLog("已卸载补丁")
                refresh("卸载后")
            } catch (e: Exception) {
                appendLog("卸载失败: ${e.message}")
            }
        }
        refresh("进入")
    }

    private fun refresh(reason: String) {
        val message = target.getMessage()
        val sum = target.add(1, 2)
        findViewById<TextView>(R.id.tvMessage).text = "getMessage = $message"
        findViewById<TextView>(R.id.tvAdd).text = "add(1,2) = $sum"
        appendLog("$reason: $message , 1+2=$sum")
    }

    private fun appendLog(line: String) {
        Log.d(TAG, line)
        findViewById<TextView>(R.id.tvLog).append(line + "\n")
    }
}
