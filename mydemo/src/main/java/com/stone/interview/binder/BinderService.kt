package com.stone.interview.binder

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log

class BinderService : Service() {

    companion object {
        private const val TAG = "BinderDemo"
    }

    private val binder = object : IBinderApi.Stub() {
        override fun getPid(): Int {
            val pid = Process.myPid()
            Log.d(TAG, "Service.getPid() pid=$pid thread=${Thread.currentThread().name}")
            return pid
        }

        override fun sendMessage(text: String): String {
            val pid = Process.myPid()
            Log.d(TAG, "Service.sendMessage() 收到 text=$text pid=$pid thread=${Thread.currentThread().name}")
            return "远程Service(pid=$pid) 已收到: $text"
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service.onCreate() pid=${Process.myPid()}")
    }

    override fun onBind(intent: Intent?): IBinder {
        Log.d(TAG, "Service.onBind() pid=${Process.myPid()}")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "Service.onUnbind() pid=${Process.myPid()}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "Service.onDestroy() pid=${Process.myPid()}")
        super.onDestroy()
    }
}
