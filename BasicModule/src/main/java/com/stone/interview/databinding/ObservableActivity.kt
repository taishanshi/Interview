package com.stone.interview.databinding

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.stone.interview.R
/**
 * DataBinding + BaseObservable。
 * 数据流：改属性 -> notifyPropertyChanged(BR.xxx) -> 布局自动刷新
 */
class ObservableActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckcodeObservableBinding
    private val observable = CheckcodeObservable()
    private val user = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkcode_observable)
        binding.checkCodeControl = observable
        binding.user = user
        binding.btnChangeUser.setOnClickListener {
            user.name = "admin"
            user.pwd = "888888"
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onDestroy() {
        observable.release()
        super.onDestroy()
    }
}
