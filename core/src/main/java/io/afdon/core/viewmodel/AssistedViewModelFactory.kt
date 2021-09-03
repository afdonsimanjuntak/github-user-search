package io.afdon.core.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

interface AssistedViewModelFactory<T : ViewModel> {

    fun create(savedStateHandle: SavedStateHandle): T
}