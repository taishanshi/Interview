package com.stone.interview.dagger

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val user: User
) : UserRepository {

    override fun loadUser(): String {
        return buildString {
            appendLine("user: ${user.name} / ${user.pwd}")
            appendLine("api user: ${api.fetchUser()}")
            appendLine(api.namedInfo())
            append("api hash: ${System.identityHashCode(api)}")
        }
    }
}
