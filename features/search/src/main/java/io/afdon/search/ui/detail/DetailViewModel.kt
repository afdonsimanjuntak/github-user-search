package io.afdon.search.ui.detail

import android.view.View
import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.afdon.core.event.Event
import io.afdon.core.extension.cancelIfActive
import io.afdon.core.viewmodel.AssistedViewModelFactory
import io.afdon.search.model.RequestResult
import io.afdon.search.model.User
import io.afdon.search.usecase.AddFavouriteUseCase
import io.afdon.search.usecase.DeleteFavouriteUseCase
import io.afdon.search.usecase.GetFavouriteUserIdsUseCase
import io.afdon.search.usecase.GetUserUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getFavouriteUserIdsUseCase: GetFavouriteUserIdsUseCase,
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

    private val _progressVisibility = MutableLiveData(View.INVISIBLE)
    val progressVisibility: LiveData<Int> = _progressVisibility

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> = _errorEvent

    private var getUserJob: Job? = null
    private var getFavouriteIdsJob: Job? = null
    private var toggleUserJob: Job? = null

    init {
        savedStateHandle.get<String>("login")?.let { getUser(it) }
    }

    private fun getUser(login: String) {
        getUserJob.cancelIfActive()
        getUserJob = viewModelScope.launch {
            getUserUseCase.getUser(login).collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) getUserJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        it.data.let { u ->
                            _user.value = u
                            getFavouriteIds()
                        }
                    }
                }
            }
        }
    }

    fun toggleFavourite() {
        _isFavourite.value?.let { favourite ->
            _user.value?.let { user_ ->
                toggleUserJob.cancelIfActive()
                toggleUserJob = viewModelScope.launch {
                    val result = if (favourite) {
                        deleteFavouriteUseCase.delete(user_)
                    } else {
                        addFavouriteUseCase.add(user_)
                    }
                    result.collect {
                        when (it) {
                            is RequestResult.Loading -> {
                                _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                                if (!it.isLoading) toggleUserJob.cancelIfActive()
                            }
                            is RequestResult.Error -> {
                                _errorEvent.value = Event(it.error)
                            }
                            is RequestResult.Success -> {
                                getFavouriteIds()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getFavouriteIds() {
        getFavouriteIdsJob.cancelIfActive()
        getFavouriteIdsJob = viewModelScope.launch {
            getFavouriteUserIdsUseCase.getFavouriteUserIds().collect {
                when (it) {
                    is RequestResult.Loading -> {
                        _progressVisibility.value = if (it.isLoading) View.VISIBLE else View.GONE
                        if (!it.isLoading) getFavouriteIdsJob.cancelIfActive()
                    }
                    is RequestResult.Error -> {
                        _errorEvent.value = Event(it.error)
                    }
                    is RequestResult.Success -> {
                        _user.value?.let { u ->
                            _isFavourite.value = it.data.contains(u.id)
                            setButtonText()
                        }
                    }
                }
            }
        }
    }

    private fun setButtonText() {
        _isFavourite.value?.let {
            _buttonText.value = if (it) "Remove from favourite" else "Add to favourite"
        }
    }
}