package com.stone.kotlin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * StateFlow 是热的，而且始终有当前值。新的收集者一订阅就能拿到这份值。
 * SharedFlow 也是热的，但默认不保留历史。收集开始之前发出的事件收不到。
 */
class FlowViewModel : ViewModel() {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    private var eventIndex = 0

    fun increment() {
        _count.update { it + 1 }
    }

    fun sendEvent() {
        eventIndex += 1
        _events.tryEmit("第 $eventIndex 次点击")
    }
}
