package io.afdon.githubusersearch.di.activity

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.afdon.core.viewmodel.AssistedViewModelFactory
import io.afdon.favourite.ui.FavouriteViewModel
import io.afdon.search.ui.detail.DetailViewModel
import io.afdon.search.ui.search.SearchViewModel

@Module
interface AbstractViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    fun bindSearchViewModelFactory(
        factory: SearchViewModel.Factory
    ): AssistedViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(DetailViewModel::class)
    fun bindDetailViewModelFactory(
        factory: DetailViewModel.Factory
    ): AssistedViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(FavouriteViewModel::class)
    fun bindFavouriteViewModelFactory(
        factory: FavouriteViewModel.Factory
    ): AssistedViewModelFactory<out ViewModel>
}