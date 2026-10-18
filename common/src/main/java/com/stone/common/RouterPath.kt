package com.stone.common

/**
 * 各业务 module 的 ARouter 路径集中放这里，避免互相直接依赖。
 * 路径必须至少两级：/group/path
 */
object RouterPath {
    const val BASIC_MAIN = "/basic/main"
    const val RESTAURANT_MAIN = "/restaurant/main"
    const val KOTLIN_MAIN = "/kotlin/main"
    const val KOTLIN_SCOPE = "/kotlin/scope"
    const val KOTLIN_FLOW = "/kotlin/flow"
    const val KOTLIN_COMPOSE = "/kotlin/compose"
    const val KOTLIN_COMPOSE_PLAYGROUND = "/kotlin/compose_playground"
    const val KOTLIN_COMPOSE_TAB = "/kotlin/compose_tab"
    const val KOTLIN_COMPOSE_CANVAS = "/kotlin/compose_canvas"
    const val KOTLIN_COMPOSE_PHOTOS = "/kotlin/compose_photos"
    const val KOTLIN_COMPOSE_SCAFFOLD = "/kotlin/compose_scaffold"
    const val KOTLIN_COMPOSE_WATERFALL = "/kotlin/compose_waterfall"
    const val KOTLIN_COMPOSE_ACCOMPANIST = "/kotlin/compose_accompanist"
    const val KOTLIN_COMPOSE_LIVEDATA = "/kotlin/compose_livedata"
    const val KOTLIN_COMPOSE_LAUNCHED_EFFECT = "/kotlin/compose_launched_effect"
    const val KOTLIN_COMPOSE_SCROLL_CONFLICT = "/kotlin/compose_scroll_conflict"
    const val KOTLIN_COMPOSE_NAVIGATION = "/kotlin/compose_navigation"
}
