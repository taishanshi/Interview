package com.stone.interview.mvi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class MviState(
    val count: Int = 0,
    val message: String = "点按钮发出 Intent"
)

sealed class MviIntent {
    data object Plus : MviIntent()
    data object Minus : MviIntent()
    data object Reset : MviIntent()
}

class MviViewModel : ViewModel() {

    private val _state = MutableLiveData(MviState())
    val state: LiveData<MviState> = _state

    fun dispatch(intent: MviIntent) {
        val current = _state.value ?: MviState()
        _state.value = reduce(current, intent)
    }

    private fun reduce(state: MviState, intent: MviIntent): MviState {
        return when (intent) {
            MviIntent.Plus -> state.copy(
                count = state.count + 1,
                message = "处理了 Plus"
            )
            MviIntent.Minus -> state.copy(
                count = state.count - 1,
                message = "处理了 Minus"
            )
            MviIntent.Reset -> MviState(message = "处理了 Reset")
        }
    }
}
