package io.afdon.favourite.repo

import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.model.User
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {

    fun getFavourites() : Flow<RequestResult<List<User>>>

    fun deleteFavourite(user: User) : Flow<RequestResult<User>>
}