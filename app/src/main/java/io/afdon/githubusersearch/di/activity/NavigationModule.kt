package io.afdon.githubusersearch.di.activity

import dagger.Binds
import dagger.Module
import io.afdon.githubusersearch.navigation.SearchNavigationImpl
import io.afdon.search.navigation.SearchNavigation

@Module
interface NavigationModule {

    @Binds
    fun bindSearchNavigation(searchNavigation: SearchNavigationImpl) : SearchNavigation
}