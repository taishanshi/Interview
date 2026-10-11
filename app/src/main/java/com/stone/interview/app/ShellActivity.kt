package com.stone.interview.app

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.stone.common.RouterPath

class ShellActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shell)

        findViewById<Button>(R.id.btnMyDemo).setOnClickListener {
            ARouter.getInstance().build(RouterPath.MYDEMO_MAIN).navigation()
        }
        findViewById<Button>(R.id.btnRestaurant).setOnClickListener {
            ARouter.getInstance().build(RouterPath.RESTAURANT_MAIN).navigation()
        }
        findViewById<Button>(R.id.btnKotlin).setOnClickListener {
            ARouter.getInstance().build(RouterPath.KOTLIN_SCOPE).navigation()
        }
        findViewById<Button>(R.id.btnFlow).setOnClickListener {
            ARouter.getInstance().build(RouterPath.KOTLIN_FLOW).navigation()
        }
    }
}
