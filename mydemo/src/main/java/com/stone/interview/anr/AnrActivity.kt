package com.stone.interview.anr

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R

class AnrActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "AnrDemo"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_anr)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnSleepAnr).setOnClickListener {
            appendLog("主线程 sleep 15 秒，不要再点屏幕，等 ANR")
            Log.d(TAG, "main thread sleep start tid=${Thread.currentThread().id}")
            Thread.sleep(15_000)
            Log.d(TAG, "main thread sleep end")
            appendLog("sleep 结束（若已 ANR 可能看不到这句）")
        }

        findViewById<Button>(R.id.btnLoopAnr).setOnClickListener {
            appendLog("主线程死循环，traces 里 main 会停在 while")
            Log.d(TAG, "main thread loop start")
            while (true) {
                SystemClock.elapsedRealtime()
            }
        }
    }

    private fun appendLog(line: String) {
        findViewById<TextView>(R.id.tvLog).append(line + "\n")
    }
}