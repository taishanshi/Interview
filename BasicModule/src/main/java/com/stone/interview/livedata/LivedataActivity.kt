package com.stone.interview.livedata

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.stone.interview.R

class LivedataActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LivedataDemo"
    }

    private val liveData = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_livedata)

        val root = findViewById<android.view.View>(R.id.main)
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

        liveData.observe(this, Observer<String> { value ->
            Log.d(TAG, "onChanged: $value  thread=${Thread.currentThread().name}")
            findViewById<TextView>(R.id.tvValue).text = value
        })
        liveData.observeForever {  }

        findViewById<Button>(R.id.btnPostValue).setOnClickListener {
            Thread {
                val text = "postValue 来自 ${Thread.currentThread().name}"
                Log.d(TAG, "postValue: $text")
                liveData.postValue(text)
            }.start()
        }

        findViewById<Button>(R.id.btnSetValue).setOnClickListener {
            val text = "setValue 来自 ${Thread.currentThread().name}"
            Log.d(TAG, "setValue: $text")
            liveData.value = text
        }
    }
}
