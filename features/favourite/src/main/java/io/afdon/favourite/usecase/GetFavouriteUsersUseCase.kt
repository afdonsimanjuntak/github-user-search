package io.afdon.favourite.usecase

import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.model.User
import kotlinx.coroutines.flow.Flow

interface GetFavouriteUsersUseCase {

    fun getFavourites() : Flow<RequestResult<List<User>>>
}