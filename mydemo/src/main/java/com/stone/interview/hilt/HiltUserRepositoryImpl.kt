package com.stone.interview.hilt

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class HiltUserRepositoryImpl @Inject constructor(
    private val user: HiltUser,
    @param:Named("welcome") private val welcome: String
) : HiltUserRepository {

    override fun load(): String = "$welcome, ${user.name}"
}
