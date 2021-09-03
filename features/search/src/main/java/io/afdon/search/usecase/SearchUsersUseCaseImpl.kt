package io.afdon.search.usecase

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import io.afdon.search.repo.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUsersUseCaseImpl @Inject constructor(
    private val searchRepository: SearchRepository
) : SearchUsersUseCase {

    override fun searchUsers(q: String, page: Int, perPage: Int): Flow<RequestResult<List<User>>> =
        searchRepository.searchUsers(q, page, perPage)
}