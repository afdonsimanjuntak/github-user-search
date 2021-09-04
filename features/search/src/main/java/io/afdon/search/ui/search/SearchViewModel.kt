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
import io.afdon.search.R
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
        private const val FIRST_PAGE = 1
        private const val SEARCH_DELAY = 500L
        private const val PER_PAGE = 30
    }

    private val _searchResultItems = MutableLiveData<List<SearchResultAdapter.Item>>(arrayListOf())
    val searchResultItems: LiveData<List<SearchResultAdapter.Item>> = _searchResultItems

    val queryLiveData = savedStateHandle.getLiveData<String>("query")

    private val _progressVisibility = MutableLiveData(View.GONE)
    val progressVisibility: LiveData<Int> = _progressVisibility

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    private val pages = mutableMapOf<Int, List<SearchResultAdapter.Item>>()
    private var currentPage = 0
    private var lastTriedPage = 0
    private var hasMore = true

    private var searchUsersJob: Job? = null
    private var toggleFavouriteJob: Job? = null
    private var getFavouriteUserIdsJob : Job? = null

    init {
        queryLiveData.value?.let { if (it.length >= 3) newSearch() }
    }

    fun newSearch() {
        queryLiveData.value?.let { newQuery ->
            if (newQuery.length >= 3) {
                searchUsersJob.cancelIfActive()
                searchUsersJob = viewModelScope.launch {
                    delay(SEARCH_DELAY)
                    fetchUsers(newQuery, FIRST_PAGE)
                }
            } else {
                _isRefreshing.value = false
            }
        } ?: run { _isRefreshing.value = false }
    }

    fun onScrollLoadMore() {
        val page = currentPage + 1
        if (page == lastTriedPage) return
        lastTriedPage = page
        loadNextPage(page)
    }

    fun onButtonLoadMore() {
        val page = currentPage + 1
        loadNextPage(page)
    }

    private fun loadNextPage(page: Int) {
        searchUsersJob.cancelIfActive()
        searchUsersJob = viewModelScope.launch {
            queryLiveData.value?.let { fetchUsers(it, page) }
        }
    }

    private suspend fun fetchUsers(query: String, page: Int) {
        if (query != queryLiveData.value) {
            _isRefreshing.value = false
            return
        }
        searchUsersUseCase.searchUsers(query, page, PER_PAGE).collect { result ->
            when (result) {
                is RequestResult.Loading -> {
                    _isRefreshing.value = result.isLoading
                    if (result.isLoading) addLoadingFooter()
                    if (!result.isLoading) searchUsersJob.cancelIfActive()
                }
                is RequestResult.Error -> {
                    addLoadMoreFooter()
                    _errorEvent.value = Event(result.error)
                }
                is RequestResult.Success -> {
                    currentPage = page
                    if (page == FIRST_PAGE) pages.clear()
                    val newItems = result.data.map { SearchResultAdapter.Item(it, false) }
                    hasMore = result.hasMore
                    pages[page] = newItems
                    updateItems()
                }
            }
        }
    }

    fun toggleFavorite(item: SearchResultAdapter.Item) {
        toggleFavouriteJob.cancelIfActive()
        toggleFavouriteJob = viewModelScope.launch {
            val result = if (item.isFavourite) {
                item.user?.let { deleteFavouriteUseCase.delete(it) }
            } else {
                item.user?.let { addFavouriteUseCase.add(it) }
            }
            result?.collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) toggleFavouriteJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        updateItems()
                    }
                }
            }
        }
    }

    fun updateItems() {
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
                        _searchResultItems.value = populateItems().map { item ->
                            SearchResultAdapter.Item(item.user, it.data.contains(item.user?.id))
                        }
                    }
                }
            }
        }
    }

    private fun populateItems() : ArrayList<SearchResultAdapter.Item> {
        val newItems = arrayListOf<SearchResultAdapter.Item>()
        for (i in FIRST_PAGE..currentPage) {
            pages[i]?.let { newItems.addAll(it) }
        }
        return newItems
    }

    private fun addLoadingFooter() {
        if (searchResultItems.value.isNullOrEmpty()) return
        _searchResultItems.value = populateItems().apply {
            add(SearchResultAdapter.Item(type = R.layout.item_recyclerview_loading))
        }
    }

    private fun addLoadMoreFooter() {
        if (searchResultItems.value.isNullOrEmpty()) return
        _searchResultItems.value = populateItems().apply {
            add(SearchResultAdapter.Item(type = R.layout.item_recyclerview_load_more))
        }
    }
}