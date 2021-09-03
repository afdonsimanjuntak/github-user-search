package io.afdon.favourite.usecase.impl

import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.model.User
import io.afdon.favourite.repo.FavouriteRepository
import io.afdon.favourite.usecase.DeleteFavouriteUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFavouriteUseCaseImpl @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) : DeleteFavouriteUseCase {

    override fun delete(user: User): Flow<RequestResult<User>> =
        favouriteRepository.deleteFavourite(user)
}