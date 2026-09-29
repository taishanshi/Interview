package com.stone.interview.dagger

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [UserModule::class, RepositoryModule::class])
interface UserComponent {
    fun activityComponent(): ActivityComponent.Factory
}
