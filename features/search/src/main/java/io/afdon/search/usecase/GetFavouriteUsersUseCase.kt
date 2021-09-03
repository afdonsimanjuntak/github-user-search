package io.afdon.search.usecase

import io.afdon.search.model.RequestResult
import kotlinx.coroutines.flow.Flow

interface GetFavouriteUsersUseCase {

    fun getFavouriteUserIds() : Flow<RequestResult<List<Int>>>
}