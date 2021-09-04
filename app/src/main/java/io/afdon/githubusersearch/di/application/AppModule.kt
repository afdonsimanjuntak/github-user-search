package io.afdon.githubusersearch.di.application

import dagger.Binds
import dagger.Module
import io.afdon.core.logger.Logger
import io.afdon.core.logger.LoggerImpl
import javax.inject.Singleton

@Module
interface AppModule {

    @Singleton
    @Binds
    fun bindLogger(logger: LoggerImpl) : Logger
}