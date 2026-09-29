package com.stone.common

/**
 * 各业务 module 的 ARouter 路径集中放这里，避免互相直接依赖。
 * 路径必须至少两级：/group/path
 */
object RouterPath {
    const val MYDEMO_MAIN = "/mydemo/main"
    const val RESTAURANT_MAIN = "/restaurant/main"
}
