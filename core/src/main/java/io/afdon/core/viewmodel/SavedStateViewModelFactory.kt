package io.afdon.core.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import dagger.Reusable
import javax.inject.Inject

@Reusable
class SavedStateViewModelFactory @Inject constructor(
    private val factories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedViewModelFactory<out ViewModel>>
) {

    fun create(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
    ): AbstractSavedStateViewModelFactory =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                factories[modelClass]?.let {
                    try {
                        return it.create(handle) as T
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                } ?: throw IllegalArgumentException("Unknown model class $modelClass")
            }
        }
}