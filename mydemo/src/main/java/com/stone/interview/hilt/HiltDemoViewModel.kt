package com.stone.interview.hilt

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HiltDemoViewModel @Inject constructor(
    private val repository: HiltUserRepository
) : ViewModel() {
    val info: String = repository.load()
}
