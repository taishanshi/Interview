package com.stone.interview.lifecycle

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R

class LifecycleDemoActivity : AppCompatActivity() {

    companion object {
        const val TAG = "LifecycleDemo"
    }

    private lateinit var tvState: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Activity onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.activity_lifecycle_demo)

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

        tvState = findViewById(R.id.tvState)
        findViewById<Button>(R.id.btnFinish).setOnClickListener { finish() }

        //lifecycle.addObserver(LifecycleEventLogger())
        lifecycle.addObserver(DefaultLifecycleEventLogger())
        //lifecycle.addObserver(LifecycleEventObserverLogger())
    }

    override fun onPause() {
        Log.d(TAG, "Activity onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "Activity onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "Activity onDestroy")
        super.onDestroy()
    }

}
