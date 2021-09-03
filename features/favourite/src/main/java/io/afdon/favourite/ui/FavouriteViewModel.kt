package io.afdon.favourite.ui

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.afdon.core.event.Event
import io.afdon.core.viewmodel.AssistedViewModelFactory
import io.afdon.favourite.model.RequestResult
import io.afdon.favourite.usecase.DeleteFavouriteUseCase
import io.afdon.favourite.usecase.GetFavouriteUsersUseCase
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

    private val _linearProgressVisibility = MutableLiveData(View.INVISIBLE)
    val linearProgressVisibility: LiveData<Int> = _linearProgressVisibility

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    fun getFavourites() {
        viewModelScope.launch {
            getFavouriteUsersUseCase.getFavourites().collect {
                when (it) {
                    is RequestResult.Loading -> {}
                    is RequestResult.Error -> {}
                    is RequestResult.Success -> {
                        _favouriteUsers.value = it.data.map { user ->
                            FavouriteAdapter.Item(user)
                        }
                    }
                }
            }
        }
    }

    fun deleteFavourite(item: FavouriteAdapter.Item) {
        viewModelScope.launch {
            deleteFavouriteUseCase.delete(item.user).collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _linearProgressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
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