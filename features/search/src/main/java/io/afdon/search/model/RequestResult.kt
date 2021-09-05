package io.afdon.search.model

import java.lang.Exception

sealed class RequestResult<T> {

    data class Loading<S>(
        val isLoading: Boolean
    ) : RequestResult<S>()

    data class Success<S>(
        val data: S,
        val hasMore: Boolean = true
    ) : RequestResult<S>()

    data class Error<S>(
        val message: String,
        val code: Int? = null,
        val exception: Exception? = null
    ) : RequestResult<S>()
}