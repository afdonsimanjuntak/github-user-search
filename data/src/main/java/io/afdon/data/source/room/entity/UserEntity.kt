package io.afdon.data.source.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val login: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "html_url") val htmlUrl: String?,
    @ColumnInfo(name = "repos_url") val reposUrl: String?
)