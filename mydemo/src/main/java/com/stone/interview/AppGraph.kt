package com.stone.interview

import com.stone.interview.dagger.DaggerUserComponent
import com.stone.interview.dagger.UserComponent

/**
 * 壳工程 Application 不是 MyApplication 时，DaggerActivity 也从这里取 UserComponent。
 */
object AppGraph {
    val userComponent: UserComponent by lazy { DaggerUserComponent.create() }
}
