package com.stone.interview.checkcode

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.stone.interview.R
import com.stone.interview.databinding.ActivityCheckcodeBinding
import com.stone.interview.checkcode.CheckcodeViewModel

/**
 * DataBinding + LiveData。
 * 数据流：改 LiveData -> DataBinding 自动刷新 UI
 */
class Checkcode : AppCompatActivity() {

    private lateinit var binding: ActivityCheckcodeBinding
    private val viewModel: CheckcodeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkcode)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
