package com.stone.interview.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HiltBindModule {

    @Binds
    @Singleton
    abstract fun bindRepository(impl: HiltUserRepositoryImpl): HiltUserRepository
}
