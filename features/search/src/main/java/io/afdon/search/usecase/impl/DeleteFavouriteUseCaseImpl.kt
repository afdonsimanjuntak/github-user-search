package io.afdon.search.usecase.impl

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import io.afdon.search.repo.SearchRepository
import io.afdon.search.usecase.DeleteFavouriteUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteFavouriteUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : DeleteFavouriteUseCase {

    override fun delete(user: User): Flow<RequestResult<User>> =
        searchRepository.deleteFavourite(user)
}