package com.stone.kotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * viewModelScope 跟着 ViewModel。
 * 旋转屏幕会新建 Activity，但不会新建这个 ViewModel，计数接着涨。
 * 页面 finish 后 ViewModel 被清掉，scope 里的协程一起取消。
 */
class ScopeViewModel : ViewModel() {

    private val _tick = MutableStateFlow(0)
    val tick: StateFlow<Int> = _tick.asStateFlow()

    private val _log = MutableStateFlow("")
    val log: StateFlow<String> = _log.asStateFlow()

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _tick.value += 1
            }
        }
    }

    fun append(line: String) {
        _log.update { previous ->
            val lines = if (previous.isEmpty()) emptyList() else previous.split("\n")
            (lines + line).takeLast(16).joinToString("\n")
        }
    }
}
