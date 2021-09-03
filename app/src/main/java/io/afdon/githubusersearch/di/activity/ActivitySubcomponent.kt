package io.afdon.githubusersearch.di.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import dagger.BindsInstance
import dagger.Subcomponent
import io.afdon.favourite.di.FavouriteModule
import io.afdon.search.di.SearchModule

@ActivityScope
@Subcomponent(modules = [
    FragmentModule::class,
    ViewModelModule::class,
    AbstractViewModelModule::class,
    NavigationModule::class,
    SearchModule::class,
    FavouriteModule::class
])
interface ActivitySubcomponent {

    @Subcomponent.Factory
    interface Factory {

        fun create(@BindsInstance appCompatActivity: AppCompatActivity) : ActivitySubcomponent
    }

    fun getFragmentFactory() : FragmentFactory
}