package io.afdon.search.repo

import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchUsers(q: String, page: Int, perPage: Int) : Flow<RequestResult<List<User>>>

    fun getFavouriteUserIds() : Flow<RequestResult<List<Int>>>

    fun addFavourite(user: User) : Flow<RequestResult<User>>

    fun deleteFavourite(user: User) : Flow<RequestResult<User>>

    suspend fun getUser(login: String) : Flow<RequestResult<User>>
}