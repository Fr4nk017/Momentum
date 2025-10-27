package com.momentum.app.ui.screens.places

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.app.data.DatabaseProvider
import com.momentum.app.data.local.RecentPlaceEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlacesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DatabaseProvider.recentPlaceRepository(application)

    val recentPlaces: StateFlow<List<RecentPlaceEntity>> = repository.getRecentPlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRecentPlace(name: String, category: String) {
        viewModelScope.launch {
            repository.addRecentPlace(name, category)
        }
    }

    fun deleteRecentPlace(id: Long) {
        viewModelScope.launch {
            repository.delete(id)
        }
    }

    fun clearAllRecent() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
