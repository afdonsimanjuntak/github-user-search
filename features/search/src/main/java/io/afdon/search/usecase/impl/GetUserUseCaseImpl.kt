package io.afdon.search.usecase.impl

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import io.afdon.search.repo.SearchRepository
import io.afdon.search.usecase.GetUserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : GetUserUseCase {

    override suspend fun getUser(login: String): Flow<RequestResult<User>> =
        searchRepository.getUser(login)
}