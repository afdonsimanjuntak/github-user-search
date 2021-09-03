package io.afdon.search.model

data class User(
    val id: Int,
    val login: String,
    val avatarUrl: String?,
    val htmlUrl: String? = null,
    val reposUrl: String? = null
)