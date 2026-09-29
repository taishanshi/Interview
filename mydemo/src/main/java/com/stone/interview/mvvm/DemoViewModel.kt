package com.stone.interview.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class DemoViewModel : ViewModel() {

    val name = MutableLiveData("同学")

    private val _count = MutableLiveData(0)
    val countText: LiveData<String> = _count.map { "点击了 $it 次" }

    fun plusOne() {
        _count.value = (_count.value ?: 0) + 1
    }

    fun reset() {
        _count.value = 0
        name.value = "同学"
    }
}
