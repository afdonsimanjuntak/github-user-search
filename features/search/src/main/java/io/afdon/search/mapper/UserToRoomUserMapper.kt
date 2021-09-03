package io.afdon.search.mapper

import io.afdon.data.mapper.Mapper
import io.afdon.data.source.room.entity.UserEntity
import io.afdon.search.model.User
import javax.inject.Inject

class UserToRoomUserMapper @Inject constructor() : Mapper<User, UserEntity> {

    override fun map(input: User?): UserEntity? {
        return input?.let {
            UserEntity(it.id, it.login, it.avatarUrl, it.htmlUrl, it.reposUrl)
        }
    }
}