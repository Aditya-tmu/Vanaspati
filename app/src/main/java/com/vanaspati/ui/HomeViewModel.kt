package com.vanaspati.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vanaspati.data.PlantRepository
import com.vanaspati.data.local.RecentSearch
import com.vanaspati.data.network.TreflePlant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = PlantRepository(app)

    // TODO: Add your Trefle API token here
    private val trefleToken = "w3FczUtOoJHGhP6dpEoOuLXt6PZclWdO3wAogb6nKqg"

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val recent: StateFlow<List<RecentSearch>> = repository.observeRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _results = MutableStateFlow<List<TreflePlant>>(emptyList())
    val results: StateFlow<List<TreflePlant>> = _results.asStateFlow()

    fun onQueryChange(text: String) { _query.value = text }

    fun search() {
        val q = query.value.trim()
        if (q.isEmpty()) return
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.addRecent(q)
                val resp = repository.search(q, trefleToken)
                _results.value = resp.data
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
