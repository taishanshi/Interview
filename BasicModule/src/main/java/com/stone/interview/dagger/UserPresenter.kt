package com.stone.interview.dagger

import javax.inject.Inject
import javax.inject.Named

@ActivityScope
class UserPresenter @Inject constructor(
    private val repository: UserRepository,
    @param:Named("screen") private val screenName: String
) {
    fun present(): String {
        return buildString {
            appendLine("screen: $screenName")
            appendLine("presenter hash: ${System.identityHashCode(this@UserPresenter)}")
            append(repository.loadUser())
        }
    }
}
