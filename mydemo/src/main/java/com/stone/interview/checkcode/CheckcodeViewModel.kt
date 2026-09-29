package com.stone.interview.checkcode

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CheckcodeViewModel : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var remainingSeconds = 0

    private val _countdownText = MutableLiveData("点击获取验证码")
    val countdownText: LiveData<String> = _countdownText

    private val _buttonText = MutableLiveData("获取验证码")
    val buttonText: LiveData<String> = _buttonText

    private val _buttonEnabled = MutableLiveData(true)
    val buttonEnabled: LiveData<Boolean> = _buttonEnabled

    private val tick = object : Runnable {
        override fun run() {
            if (remainingSeconds > 0) {
                _countdownText.value = "${remainingSeconds}s 后可重新获取"
                _buttonText.value = "${remainingSeconds}s"
                remainingSeconds--
                handler.postDelayed(this, 1000)
            } else {
                resetUi()
            }
        }
    }

    fun startCountdown() {
        if (_buttonEnabled.value == false) return
        remainingSeconds = 60
        _buttonEnabled.value = false
        handler.removeCallbacks(tick)
        handler.post(tick)
    }

    private fun resetUi() {
        remainingSeconds = 0
        _buttonEnabled.value = true
        _buttonText.value = "获取验证码"
        _countdownText.value = "点击获取验证码"
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(null)
        super.onCleared()
    }
}