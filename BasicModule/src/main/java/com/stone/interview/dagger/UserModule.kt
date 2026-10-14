package com.stone.interview.dagger

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Named
import javax.inject.Singleton

@DisableInstallInCheck
@Module
class UserModule {

    @Provides
    @Singleton
    fun provideUser(): User = User("wyx", "123456")

    @Provides
    @Named("baseUrl")
    fun provideBaseUrl(): String = "https://api.example.com"

    @Provides
    @Named("token")
    fun provideToken(): String = "abc123"
}
