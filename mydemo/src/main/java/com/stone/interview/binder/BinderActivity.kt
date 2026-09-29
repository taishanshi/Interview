package com.stone.interview.binder

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R
import com.stone.interview.binder.IBinderApi

class BinderActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BinderDemo"
    }

    private var api: IBinderApi? = null
    private var bound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Activity.onServiceConnected() pid=${Process.myPid()} thread=${Thread.currentThread().name}")
            api = IBinderApi.Stub.asInterface(service)
            bound = true
            val remotePid = api?.pid
            findViewById<TextView>(R.id.tvPid).text =
                "Activity pid=${Process.myPid()}\nService pid=$remotePid"
            appendLog("绑定成功，可以发消息")

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Activity.onServiceDisconnected() pid=${Process.myPid()}")
            api = null
            bound = false
            appendLog("Service 进程断开")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_binder)
        Log.d(TAG, "Activity.onCreate() pid=${Process.myPid()} process=${Application.getProcessName()}")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.tvPid).text = "Activity pid=${Process.myPid()}\n还未绑定"
        findViewById<Button>(R.id.btnSend).setOnClickListener { send() }
        bindService(Intent(this, BinderService::class.java), connection, BIND_AUTO_CREATE)
    }

    private fun send() {
        val input = findViewById<EditText>(R.id.etMessage)
        val text = input.text.toString().ifBlank { "hello" }
        Log.d(TAG, "Activity 准备调用 AIDL sendMessage() text=$text pid=${Process.myPid()} thread=${Thread.currentThread().name}")
        try {
            val result = api?.sendMessage(text)
            Log.d(TAG, "Activity 收到返回 result=$result pid=${Process.myPid()} thread=${Thread.currentThread().name}")
            appendLog("发出: $text")
            appendLog("返回: $result")
            input.setText("")
        } catch (e: Exception) {
            Log.e(TAG, "AIDL 调用失败", e)
            appendLog("发送失败: ${e.message}")
        }
    }

    private fun appendLog(line: String) {
        findViewById<TextView>(R.id.tvLog).append(line + "\n")
    }

    override fun onDestroy() {
        Log.d(TAG, "Activity.onDestroy() bound=$bound pid=${Process.myPid()}")
        if (bound) {
            unbindService(connection)
            bound = false
        }
        super.onDestroy()
    }
}