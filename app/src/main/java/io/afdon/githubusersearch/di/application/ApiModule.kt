package io.afdon.githubusersearch.di.application

import dagger.Module
import dagger.Provides
import io.afdon.data.source.api.GithubApi
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object ApiModule {

    @Singleton
    @Provides
    fun provideGithubApi(retrofit: Retrofit) : GithubApi = retrofit.create(GithubApi::class.java)
}