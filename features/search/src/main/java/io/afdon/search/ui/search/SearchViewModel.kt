package io.afdon.search.ui.search

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.afdon.core.event.Event
import io.afdon.core.viewmodel.AssistedViewModelFactory
import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import io.afdon.search.usecase.AddFavouriteUseCase
import io.afdon.search.usecase.DeleteFavouriteUseCase
import io.afdon.search.usecase.SearchUsersUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SearchViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
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

    private val _message = savedStateHandle.getLiveData<String?>("message", null)
    val message: LiveData<String?> = _message

    private val _linearProgressVisibility = MutableLiveData(View.INVISIBLE)
    val linearProgressVisibility: LiveData<Int> = _linearProgressVisibility

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var page = 1
    private val perPage = 100

    private val _searchResultItems = MutableLiveData<List<SearchResultAdapter.Item>>()
    val searchResultItems: LiveData<List<SearchResultAdapter.Item>> = _searchResultItems

    init {
        query.value?.let {
            if (it.length >= 3 && searchResultItems.value.isNullOrEmpty()) {
                searchUser()
            }
        }
    }

    fun searchUser() {
        query.value?.let { currentQuery ->
            prevQuery = currentQuery
            if (currentQuery.length < 3) return@let
            viewModelScope.launch {
                delay(SEARCH_DELAY)
                if (currentQuery == prevQuery) {
                    searchUsersUseCase.searchUsers(
                        currentQuery, page, perPage
                    ).collect {
                        handleSearchResult(it)
                    }
                }
            }
        }
    }

    private fun handleSearchResult(result: RequestResult<List<User>>) {
        when (result) {
            is RequestResult.Loading -> {
                _isLoading.value = result.isLoading
            }
            is RequestResult.Error -> {
                _message.value = result.error
                _searchResultItems.value = arrayListOf()
                Log.d("---------------------", "handleSearchResult: ${_message.value}")
            }
            is RequestResult.Success -> {
                val newItems = result.data.map { SearchResultAdapter.Item(it, false) }
                _searchResultItems.value = newItems
            }
        }
    }

    fun toggleFavorite(item: SearchResultAdapter.Item) {
        Log.d("---------------------", "toggleFavorite: test1")
        viewModelScope.launch {
            Log.d("---------------------", "toggleFavorite: test2")
            val result = if (item.isFavourite) {
                deleteFavouriteUseCase.delete(item.user)
            } else {
                addFavouriteUseCase.add(item.user)
            }
            result.collect {
                Log.d("---------------------", "toggleFavorite: test3")
                when (it) {
                    is RequestResult.Loading -> {
                        Log.d("---------------------", "toggleFavorite: toggle loading")
                        _linearProgressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                    }
                    is RequestResult.Error -> {
                        Log.d("---------------------", "toggleFavorite: toggle error")
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        Log.d("---------------------", "toggleFavorite: toggle success")
                        _searchResultItems.value?.let { items ->
                            val newItems = arrayListOf<SearchResultAdapter.Item>()
                            newItems.addAll(items)
                            val index = items.indexOf(item)
                            newItems[index].isFavourite = !item.isFavourite
                            _searchResultItems.value = newItems
                        }
                    }
                }
            }
        }
    }
}