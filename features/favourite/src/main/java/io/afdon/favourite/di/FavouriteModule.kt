package io.afdon.favourite.di

import dagger.Binds
import dagger.Module
import io.afdon.favourite.repo.FavouriteRepository
import io.afdon.favourite.repo.FavouriteRepositoryImpl
import io.afdon.favourite.usecase.DeleteFavouriteUseCase
import io.afdon.favourite.usecase.impl.DeleteFavouriteUseCaseImpl
import io.afdon.favourite.usecase.GetFavouriteUsersUseCase
import io.afdon.favourite.usecase.impl.GetFavouriteUsersUseCaseImpl

@Module
interface FavouriteModule {

    @Binds
    fun bindFavouriteRepository(repo: FavouriteRepositoryImpl) : FavouriteRepository

    @Binds
    fun bindGetFavouriteUsersUseCase(useCase: GetFavouriteUsersUseCaseImpl) : GetFavouriteUsersUseCase

    @Binds
    fun bindDeleteToFavouriteUseCase(useCase: DeleteFavouriteUseCaseImpl) : DeleteFavouriteUseCase
}