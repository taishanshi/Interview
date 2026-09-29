package com.stone.interview.handler

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R
import java.lang.ref.WeakReference

class HandlerActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "HandlerLeak"
        private const val MSG_DELAY = 1
    }

    private val handler = SafeHandler(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_handler)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnDelay).setOnClickListener {
            appendLog("5 秒后发消息，期间可按返回")
            val msg = handler.obtainMessage(MSG_DELAY, "延迟消息")
            handler.sendMessageDelayed(msg, 5000)
        }
        findViewById<Button>(R.id.btnFinish).setOnClickListener { finish() }
    }

    fun onDelayMessage(text: String) {
        Log.d(TAG, "收到消息: $text")
        appendLog("收到: $text")
    }

    private fun appendLog(line: String) {
        findViewById<TextView>(R.id.tvLog).append(line + "\n")
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy。延迟消息若还在队列里，也只会拿到 null 的 Activity")
        super.onDestroy()
    }

    private class SafeHandler(activity: HandlerActivity) : Handler(Looper.getMainLooper()) {
        private val activityRef = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            val activity = activityRef.get()
            if (activity == null) {
                Log.d(TAG, "Activity 已被回收，丢弃消息 what=${msg.what}")
                return
            }
            if (msg.what == MSG_DELAY) {
                activity.onDelayMessage(msg.obj as String)
            }
        }
    }
}
