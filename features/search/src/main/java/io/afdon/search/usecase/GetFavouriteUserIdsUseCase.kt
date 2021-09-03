package io.afdon.search.usecase

import io.afdon.search.model.RequestResult
import kotlinx.coroutines.flow.Flow

interface GetFavouriteUserIdsUseCase {

    fun getFavouriteUserIds() : Flow<RequestResult<Set<Int>>>
}