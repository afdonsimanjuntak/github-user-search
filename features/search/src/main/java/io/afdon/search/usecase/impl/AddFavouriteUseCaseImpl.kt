package io.afdon.search.usecase.impl

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import io.afdon.search.repo.SearchRepository
import io.afdon.search.usecase.AddFavouriteUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFavouriteUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : AddFavouriteUseCase {

    override fun add(user: User): Flow<RequestResult<User>> =
        searchRepository.addFavourite(user)
}