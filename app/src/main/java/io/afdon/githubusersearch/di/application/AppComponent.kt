package io.afdon.githubusersearch.di.application

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import io.afdon.githubusersearch.di.activity.ActivitySubcomponent
import javax.inject.Singleton

@Singleton
@Component(modules = [
    SubcomponentModule::class,
    ApiModule::class,
    RetrofitModule::class,
    RoomModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context) : AppComponent
    }

    fun getActivitySubcomponentFactory() : ActivitySubcomponent.Factory
}