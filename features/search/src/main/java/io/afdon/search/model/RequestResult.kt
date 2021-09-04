package io.afdon.search.model

sealed class RequestResult<T> {

    class Loading<S>(val isLoading: Boolean) : RequestResult<S>()

    class Success<S>(val data: S, val hasMore: Boolean = true) : RequestResult<S>()

    class Error<S>(val error: String) : RequestResult<S>()
}