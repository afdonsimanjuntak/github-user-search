package io.afdon.search.usecase

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import kotlinx.coroutines.flow.Flow

interface AddFavouriteUseCase {

    fun add(user: User) : Flow<RequestResult<User>>
}