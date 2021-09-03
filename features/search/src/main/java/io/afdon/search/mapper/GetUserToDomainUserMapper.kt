package io.afdon.search.mapper

import io.afdon.data.mapper.Mapper
import io.afdon.data.source.api.response.GetUserResponse
import io.afdon.search.model.User
import java.lang.Exception
import javax.inject.Inject

class GetUserToDomainUserMapper @Inject constructor() : Mapper<GetUserResponse, User> {

    override fun map(input: GetUserResponse?): User? {
        return input?.let {
            try {
                User(
                    it.id as Int,
                    it.login as String,
                    it.avatarUrl,
                    it.htmlUrl,
                    it.reposUrl
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}