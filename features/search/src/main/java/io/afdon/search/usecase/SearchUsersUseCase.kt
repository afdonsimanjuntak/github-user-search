package io.afdon.search.usecase

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import kotlinx.coroutines.flow.Flow

interface SearchUsersUseCase {

    fun searchUsers(q: String, page: Int, perPage: Int) : Flow<RequestResult<List<User>>>
}