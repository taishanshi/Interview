package com.stone.interview.lifecycle

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * 所有生命周期事件进同一个 onStateChanged。
 * 适合按事件分支，而不是像 DefaultLifecycleObserver 那样每个阶段一个方法。
 */
class LifecycleEventObserverLogger : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(TAG, "LifecycleEventObserver $event")
    }

    private companion object {
        const val TAG = "LifecycleDemo"
    }
}
