package io.afdon.search.ui.detail

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
import io.afdon.search.usecase.GetFavouriteUsersUseCase
import io.afdon.search.usecase.GetUserUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getFavouriteUsersUseCase: GetFavouriteUsersUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val addFavouriteUseCase: AddFavouriteUseCase,
    private val deleteFavouriteUseCase: DeleteFavouriteUseCase
): ViewModel() {

    @AssistedFactory
    interface Factory : AssistedViewModelFactory<DetailViewModel>

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isFavourite = MutableLiveData(false)
    val isFavourite: LiveData<Boolean> = _isFavourite

    private val _buttonText = MutableLiveData<String>()
    val buttonText: LiveData<String> = _buttonText

    private val _linearProgressVisibility = MutableLiveData(View.INVISIBLE)
    val linearProgressVisibility: LiveData<Int> = _linearProgressVisibility

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    init {
        savedStateHandle.get<String>("login")?.let {
            viewModelScope.launch {
                getUserUseCase.getUser(it).collect {
                    when (it) {
                        is RequestResult.Loading -> {
                            _linearProgressVisibility.value = if (it.isLoading) {
                                View.VISIBLE
                            } else {
                                View.GONE
                            }
                        }
                        is RequestResult.Error -> {
                            _errorEvent.value = Event(it.error)
                        }
                        is RequestResult.Success -> {
                            it.data.let { u ->
                                Log.d("---------------------", "$u: ")
                                _user.value = u
                                getFavouriteIds()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getFavouriteIds() {
        viewModelScope.launch {
            getFavouriteUsersUseCase.getFavouriteUserIds().collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _linearProgressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        _user.value?.let { u ->
                            var newIsFavourite = false
                            for (id in it.data) {
                                if (u.id == id) newIsFavourite = true
                                break
                            }
                            _isFavourite.value = newIsFavourite
                            setButtonText()
                        }
                    }
                }
            }
        }
    }

    fun toggleFavourite() {
        _isFavourite.value?.let { f ->
            _user.value?.let { u ->
                viewModelScope.launch {
                    val result = if (f) {
                        deleteFavouriteUseCase.delete(u)
                    } else {
                        addFavouriteUseCase.add(u)
                    }
                    result.collect {
                        when (it) {
                            is RequestResult.Loading -> {
                                _linearProgressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                            }
                            is RequestResult.Error -> {
                                _errorEvent.value = Event(it.error)
                            }
                            is RequestResult.Success -> {
                                _isFavourite.value = !f
                                setButtonText()
                                getFavouriteIds()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setButtonText() {
        Log.d("---------------------", "setButtonText:")
        _isFavourite.value?.let {
            Log.d("---------------------", "setButtonText: 1")
            if (it) {
                Log.d("---------------------", "setButtonText: 2")
                _buttonText.value = "Remove from favourite"
            } else {
                Log.d("---------------------", "setButtonText: 3")
                _buttonText.value = "Add to favourite"
            }
        }
    }
}