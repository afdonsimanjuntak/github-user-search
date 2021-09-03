package io.afdon.favourite.usecase

import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.model.User
import kotlinx.coroutines.flow.Flow

interface DeleteFavouriteUseCase {

    fun delete(user: User) : Flow<RequestResult<User>>
}