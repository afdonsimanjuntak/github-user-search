package io.afdon.data.source.api

import io.afdon.data.source.api.response.GetUserResponse
import io.afdon.data.source.api.response.SearchUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {

    @GET("/search/users")
    suspend fun searchUsers(
        @Query("q") q: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ) : Response<SearchUserResponse>

    @GET("/users/{login}")
    suspend fun getUser(
        @Path("login") login: String
    ) : Response<GetUserResponse>
}