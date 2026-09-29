package com.stone.interview.databinding

import android.os.Handler
import android.os.Looper
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.stone.interview.BR

class CheckcodeObservable : BaseObservable() {

    private val handler = Handler(Looper.getMainLooper())
    private var remainingSeconds = 0

    @get:Bindable
    var countdownText: String = "点击获取验证码"
        set(value) {
            field = value
            notifyPropertyChanged(BR.countdownText)
        }

    @get:Bindable
    var buttonText: String = "获取验证码"
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonText)
        }

    @get:Bindable
    var buttonEnabled: Boolean = true
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonEnabled)
        }

    private val tick = object : Runnable {
        override fun run() {
            if (remainingSeconds > 0) {
                countdownText = "${remainingSeconds}s 后可重新获取"
                buttonText = "${remainingSeconds}s"
                remainingSeconds--
                handler.postDelayed(this, 1000)
            } else {
                resetUi()
            }
        }
    }

    fun startCountdown() {
        if (!buttonEnabled) return
        remainingSeconds = 60
        buttonEnabled = false
        handler.removeCallbacks(tick)
        handler.post(tick)
    }

    private fun resetUi() {
        remainingSeconds = 0
        buttonEnabled = true
        buttonText = "获取验证码"
        countdownText = "点击获取验证码"
    }

    fun release() {
        handler.removeCallbacksAndMessages(null)
    }
}
