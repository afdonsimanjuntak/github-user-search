package io.afdon.search.usecase.impl

import io.afdon.search.model.RequestResult
import io.afdon.search.repo.SearchRepository
import io.afdon.search.usecase.GetFavouriteUserIdsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteUserIdsUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : GetFavouriteUserIdsUseCase {

    override fun getFavouriteUserIds(): Flow<RequestResult<Set<Int>>> =
        searchRepository.getFavouriteUserIds()
}