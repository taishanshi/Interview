package com.stone.interview.hotfix

import android.util.Log

class HotfixTarget {

    // 编译后每个方法开头会多一段：changeQuickRedirect != null 则走补丁
    fun getMessage(): String {
        Log.d("Hotfix", "原始 getMessage")
        return "原始编译代码"
    }

    fun add(a: Int, b: Int): Int {
        Log.d("Hotfix", "原始 add $a+$b")
        return a + b
    }
}
