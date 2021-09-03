package io.afdon.githubusersearch.di.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.afdon.favourite.ui.FavouriteFragment
import io.afdon.search.ui.detail.DetailFragment
import io.afdon.search.ui.search.SearchFragment

@Module
interface FragmentModule {

    @Binds
    @ActivityScope
    fun bindFragmentFactory(appFragmentFactory: AppFragmentFactory) : FragmentFactory

    @Binds
    @IntoMap
    @FragmentKey(SearchFragment::class)
    fun bindSearchFragment(searchFragment: SearchFragment) : Fragment

    @Binds
    @IntoMap
    @FragmentKey(DetailFragment::class)
    fun bindDetailFragment(searchFragment: DetailFragment) : Fragment

    @Binds
    @IntoMap
    @FragmentKey(FavouriteFragment::class)
    fun bindFavouriteFragment(searchFragment: FavouriteFragment) : Fragment
}