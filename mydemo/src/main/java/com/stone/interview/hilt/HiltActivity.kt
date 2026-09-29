package com.stone.interview.hilt

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.stone.interview.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HiltActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: HiltUserRepository

    @Inject
    @field:Named("welcome")
    lateinit var welcome: String

    private val viewModel: HiltDemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hilt)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.tvInfo).text = buildString {
            appendLine("字段注入: ${repository.load()}")
            appendLine("@Named welcome: $welcome")
            append("ViewModel: ${viewModel.info}")
        }
    }
}
