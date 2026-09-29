package com.stone.interview.dagger

import dagger.Module
import dagger.Provides
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Named

@DisableInstallInCheck
@Module
class ActivityModule {

    @Provides
    @ActivityScope
    @Named("screen")
    fun provideScreenName(): String = "DaggerActivity"
}
