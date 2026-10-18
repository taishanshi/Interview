package com.stone.interview.lifecycle

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @OnLifecycleEvent 的替代。方法是接口上的普通回调，不再走注解处理。
 * 仍然用 lifecycle.addObserver 注册。
 */
class DefaultLifecycleEventLogger : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        Log.d(TAG, "DefaultLifecycleObserver ON_CREATE")
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "DefaultLifecycleObserver ON_START")
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "DefaultLifecycleObserver ON_RESUME")
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "DefaultLifecycleObserver ON_PAUSE")
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "DefaultLifecycleObserver ON_STOP")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "DefaultLifecycleObserver ON_DESTROY")
    }

    private companion object {
        const val TAG = "LifecycleDemo"
    }
}
