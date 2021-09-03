package io.afdon.search.usecase

import io.afdon.search.model.RequestResult
import io.afdon.search.repo.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavouriteUsersUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : GetFavouriteUsersUseCase {

    override fun getFavouriteUserIds(): Flow<RequestResult<List<Int>>> =
        searchRepository.getFavouriteUserIds()
}