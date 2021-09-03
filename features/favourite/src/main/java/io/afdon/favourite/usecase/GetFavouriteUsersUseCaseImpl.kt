package io.afdon.favourite.usecase

import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.model.User
import io.afdon.favourite.repo.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteUsersUseCaseImpl @Inject constructor(
    private val favouriteRepository: FavouriteRepository
) : GetFavouriteUsersUseCase {

    override fun getFavourites(): Flow<RequestResult<List<User>>> =
        favouriteRepository.getFavourites()
}