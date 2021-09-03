package io.afdon.githubusersearch.di.application

import dagger.Module
import io.afdon.githubusersearch.di.activity.ActivitySubcomponent

@Module(subcomponents = [
    ActivitySubcomponent::class
])
object SubcomponentModule