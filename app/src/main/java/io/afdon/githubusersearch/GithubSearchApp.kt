package io.afdon.githubusersearch

import android.app.Application
import io.afdon.githubusersearch.di.application.AppComponent
import io.afdon.githubusersearch.di.application.DaggerAppComponent

class GithubSearchApp : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}