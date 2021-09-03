package io.afdon.githubusersearch.di.activity

import dagger.Binds
import dagger.Module
import io.afdon.favourite.navigation.FavouriteNavigation
import io.afdon.githubusersearch.navigation.FavouriteNavigationImpl
import io.afdon.githubusersearch.navigation.SearchNavigationImpl
import io.afdon.search.navigation.SearchNavigation

@Module
interface NavigationModule {

    @Binds
    fun bindSearchNavigation(navigation: SearchNavigationImpl) : SearchNavigation

    @Binds
    fun bindFavouriteNavigation(navigation: FavouriteNavigationImpl) : FavouriteNavigation
}