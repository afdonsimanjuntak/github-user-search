package io.afdon.search.mapper

import io.afdon.data.mapper.Mapper
import io.afdon.data.source.api.response.SearchUserResponse
import io.afdon.search.model.User
import java.lang.Exception
import javax.inject.Inject

class SearchItemToUserMapper @Inject constructor() : Mapper<SearchUserResponse.Item, User> {

    override fun map(input: SearchUserResponse.Item?): User? {
        return try {
            User(
                input?.id as Int,
                input.login as String,
                input.avatarUrl,
                input.htmlUrl,
                input.reposUrl
            )
        } catch (e: Exception) {
            null
        }
    }
}