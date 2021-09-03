package io.afdon.search.mapper

import io.afdon.data.mapper.Mapper
import io.afdon.data.source.room.entity.UserEntity
import io.afdon.search.model.User
import javax.inject.Inject

class RoomUserToDomainUserMapper @Inject constructor(): Mapper<UserEntity, User> {

    override fun map(input: UserEntity?): User? {
        return input?.let {
            User(
                it.id,
                it.login,
                it.avatarUrl,
                it.htmlUrl,
                it.reposUrl
            )
        }
    }
}