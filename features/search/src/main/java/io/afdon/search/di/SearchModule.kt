package io.afdon.search.di

import dagger.Binds
import dagger.Module
import io.afdon.search.repo.SearchRepository
import io.afdon.search.repo.SearchRepositoryImpl
import io.afdon.search.usecase.*

@Module
interface SearchModule {

    @Binds
    fun bindSearchRepository(repo: SearchRepositoryImpl) : SearchRepository

    @Binds
    fun bindSearchUserUseCase(useCase: SearchUsersUseCaseImpl) : SearchUsersUseCase

    @Binds
    fun bindAddToFavouriteUseCase(useCase: AddFavouriteUseCaseImpl) : AddFavouriteUseCase

    @Binds
    fun bindDeleteToFavouriteUseCase(useCase: DeleteFavouriteUseCaseImpl) : DeleteFavouriteUseCase

    @Binds
    fun bindGetUserUseCase(useCase: GetUserUseCaseImpl) : GetUserUseCase

    @Binds
    fun bindGetFavouriteUsersUseCase(useCase: GetFavouriteUsersUseCaseImpl) : GetFavouriteUsersUseCase
}