package io.afdon.search.ui.search

import android.view.View
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.afdon.core.event.Event
import io.afdon.core.extension.cancelIfActive
import io.afdon.core.viewmodel.AssistedViewModelFactory
import io.afdon.search.model.RequestResult
import io.afdon.search.usecase.AddFavouriteUseCase
import io.afdon.search.usecase.DeleteFavouriteUseCase
import io.afdon.search.usecase.GetFavouriteUserIdsUseCase
import io.afdon.search.usecase.SearchUsersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getFavouriteUserIdsUseCase: GetFavouriteUserIdsUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val deleteFavouriteUseCase: DeleteFavouriteUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<SearchViewModel>

    companion object {
        private const val SEARCH_DELAY = 500L
    }

    val query = savedStateHandle.getLiveData<String>("query")
    private lateinit var prevQuery : String

    private val _progressVisibility = MutableLiveData(View.GONE)
    val progressVisibility: LiveData<Int> = _progressVisibility

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    private var page = 1
    private val perPage = 100

    private val _searchResultItems = MutableLiveData<List<SearchResultAdapter.Item>>()
    val searchResultItems: LiveData<List<SearchResultAdapter.Item>> = _searchResultItems

    private var searchUsersJob: Job? = null
    private var toggleFavouriteJob: Job? = null
    private var getFavouriteUserIdsJob : Job? = null

    init {
        query.value?.let { if (it.length >= 3) searchUser() }
    }

    fun searchUser() {
        query.value?.let { currentQuery ->
            prevQuery = currentQuery
            if (currentQuery.length < 3) {
                _isLoading.value = false
                return@let
            }
            searchUsersJob.cancelIfActive()
            searchUsersJob = viewModelScope.launch {
                delay(SEARCH_DELAY)
                if (currentQuery == prevQuery) {
                    searchUsersUseCase.searchUsers(currentQuery, page, perPage).collect { result ->
                        when (result) {
                            is RequestResult.Loading -> {
                                _isLoading.value = result.isLoading
                                if (!result.isLoading) searchUsersJob.cancelIfActive()
                            }
                            is RequestResult.Error -> {
                                _errorEvent.value = Event(result.error)
                            }
                            is RequestResult.Success -> {
                                _searchResultItems.value = result.data.map {
                                    SearchResultAdapter.Item(it, false)
                                }
                                getFavouriteUserIds()
                            }
                        }
                    }
                } else {
                    _isLoading.value = false
                }
            }
        } ?: run { _isLoading.value = false }
    }

    fun toggleFavorite(item: SearchResultAdapter.Item) {
        toggleFavouriteJob.cancelIfActive()
        toggleFavouriteJob = viewModelScope.launch {
            val result = if (item.isFavourite) {
                deleteFavouriteUseCase.delete(item.user)
            } else {
                addFavouriteUseCase.add(item.user)
            }
            result.collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) toggleFavouriteJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        getFavouriteUserIds()
                    }
                }
            }
        }
    }

    fun getFavouriteUserIds() {
        getFavouriteUserIdsJob.cancelIfActive()
        getFavouriteUserIdsJob = viewModelScope.launch {
            getFavouriteUserIdsUseCase.getFavouriteUserIds().collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) getFavouriteUserIdsJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        _searchResultItems.value = _searchResultItems.value?.map { item ->
                            SearchResultAdapter.Item(item.user, it.data.contains(item.user.id))
                        }
                    }
                }
            }
        }
    }
}