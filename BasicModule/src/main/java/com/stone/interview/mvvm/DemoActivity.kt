package com.stone.interview.mvvm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.stone.interview.R
import com.stone.interview.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoBinding
    private val viewModel: DemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_demo)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        val padL = binding.main.paddingLeft
        val padT = binding.main.paddingTop
        val padR = binding.main.paddingRight
        val padB = binding.main.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left + padL,
                systemBars.top + padT,
                systemBars.right + padR,
                systemBars.bottom + padB
            )
            insets
        }
    }
}
