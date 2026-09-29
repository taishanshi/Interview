package com.stone.interview.dagger

import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(
    @param:Named("baseUrl") private val baseUrl: String,
    @param:Named("token") private val token: String
) {
    fun fetchUser(): String = "wyx"

    fun namedInfo(): String = "baseUrl=$baseUrl  token=$token"
}
