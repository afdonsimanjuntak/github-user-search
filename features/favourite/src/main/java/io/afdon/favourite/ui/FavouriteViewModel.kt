package io.afdon.favourite.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.afdon.core.event.Event
import io.afdon.core.extension.cancelIfActive
import io.afdon.core.viewmodel.AssistedViewModelFactory
import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.usecase.DeleteFavouriteUseCase
import io.afdon.favourite.usecase.GetFavouriteUsersUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getFavouriteUsersUseCase: GetFavouriteUsersUseCase,
    private val deleteFavouriteUseCase: DeleteFavouriteUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<FavouriteViewModel>

    private val _favouriteUsers = MutableLiveData<List<FavouriteAdapter.Item>>()
    val favouriteUsers: LiveData<List<FavouriteAdapter.Item>> = _favouriteUsers

    private val _progressVisibility = MutableStateFlow(View.GONE)
    val progressVisibility: StateFlow<Int> = _progressVisibility

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    private var getFavouriteUsersJob: Job? = null
    private var deleteFavouriteUserJob: Job? = null

    fun getFavourites() {
        getFavouriteUsersJob.cancelIfActive()
        getFavouriteUsersJob = viewModelScope.launch {
            getFavouriteUsersUseCase.getFavourites().collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) getFavouriteUsersJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        _favouriteUsers.value = it.data.map { user -> FavouriteAdapter.Item(user) }
                    }
                }
            }
        }
    }

    fun deleteFavourite(item: FavouriteAdapter.Item) {
        deleteFavouriteUserJob.cancelIfActive()
        deleteFavouriteUserJob = viewModelScope.launch {
            deleteFavouriteUseCase.delete(item.user).collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) deleteFavouriteUserJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        getFavourites()
                    }
                }
            }
        }
    }
}