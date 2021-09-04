package io.afdon.search.ui.search

import android.util.Log
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
        private const val PER_PAGE = 30
    }

    private val pages = mutableMapOf<Int, List<SearchResultAdapter.Item>>()
    private val _searchResultItems = MutableLiveData<List<SearchResultAdapter.Item>>(arrayListOf())
    val searchResultItems: LiveData<List<SearchResultAdapter.Item>> = _searchResultItems

    val queryEditTextValue = savedStateHandle.getLiveData<String>("query")

    private val _progressVisibility = MutableLiveData(View.GONE)
    val progressVisibility: LiveData<Int> = _progressVisibility

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    private lateinit var currentQuery : String
    private var currentPage = 0
    private var searchUsersJobMap = mutableMapOf<Int, Job?>()
    private var toggleFavouriteJob: Job? = null
    private var getFavouriteUserIdsJob : Job? = null
    private var hasMore = true
    private var lastLoadSuccesThreshold = 0
    private var currentLoadThreshold = 0

    init {
        queryEditTextValue.value?.let { if (it.length >= 3) newSearch() }
    }

    fun newSearch() {
        queryEditTextValue.value?.let { newQuery ->
            currentQuery = newQuery
            if (newQuery.length < 3) {
                _isRefreshing.value = false
            } else {
                currentPage = 1
                searchUsersJobMap[currentPage].cancelIfActive()
                searchUsersJobMap[currentPage] = viewModelScope.launch {
                    delay(SEARCH_DELAY)
                    if (newQuery == currentQuery) {
                        fetchUsers(currentQuery, currentPage, PER_PAGE, searchUsersJobMap[currentPage])
                    } else {
                        _isRefreshing.value = false
                    }
                }
            }
        } ?: run { _isRefreshing.value = false }
    }

    fun loadNextPage(loadThreshold: Int) {
        if (!hasMore || currentLoadThreshold == loadThreshold) return
        currentLoadThreshold = loadThreshold
        currentPage++
        searchUsersJobMap[currentPage].cancelIfActive()
        searchUsersJobMap[currentPage] = viewModelScope.launch {
            fetchUsers(currentQuery, currentPage, PER_PAGE, searchUsersJobMap[currentPage])
        }
    }

    private suspend fun fetchUsers(query: String, page: Int, perPage: Int, job: Job?) {
        searchUsersUseCase.searchUsers(query, page, perPage).collect { result ->
            when (result) {
                is RequestResult.Loading -> {
                    _isRefreshing.value = result.isLoading
                    if (!result.isLoading) job.cancelIfActive()
                }
                is RequestResult.Error -> {
                    lastLoadSuccesThreshold = 0
                    _errorEvent.value = Event(result.error)
                }
                is RequestResult.Success -> {
                    lastLoadSuccesThreshold = currentLoadThreshold
                    if (page == 0) pages.clear()
                    val newItems = result.data.map { SearchResultAdapter.Item(it, false) }
                    if (newItems.isEmpty()) {
                        hasMore = false
                    } else {
                        hasMore = true
                        pages[page] = newItems
                        populateItems()
                        getFavouriteUserIds()
                    }
                }
            }
        }
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

    @Suppress("ReplaceManualRangeWithIndicesCalls")
    private fun populateItems() {
        val newItems = arrayListOf<SearchResultAdapter.Item>()
        for (i in 1..currentPage) {
            pages[i]?.let { newItems.addAll(it) }
        }
        Log.d("---------------------", "populateItems: size: ${newItems.size}")
        _searchResultItems.value = newItems
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