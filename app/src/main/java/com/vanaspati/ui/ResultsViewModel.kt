package com.vanaspati.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vanaspati.data.PlantRepository
import com.vanaspati.data.PreferencesStore
import com.vanaspati.data.network.TreflePlant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ResultsViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = PlantRepository(app)
    private val preferencesStore = PreferencesStore(app)

    // TODO: Add your Trefle API token here
    private val trefleToken = "w3FczUtOoJHGhP6dpEoOuLXt6PZclWdO3wAogb6nKqg"

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _plant = MutableStateFlow<TreflePlant?>(null)
    val plant: StateFlow<TreflePlant?> = _plant.asStateFlow()

    private val _details = MutableStateFlow<String?>(null)
    val details: StateFlow<String?> = _details.asStateFlow()

    fun searchByName(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Get the selected language
                val language = preferencesStore.languageFlow.first()
                val languageName = if (language == "hi") "Hindi" else "English"

                // First, search for the plant on Trefle
                val searchResult = repository.search(query, trefleToken)
                val plant = searchResult.data.firstOrNull()
                _plant.value = plant

                // Then, get the details from Gemini
                if (plant != null) {
                    val plantName = plant.common_name ?: plant.scientific_name
                    val prompt = "Provide a detailed description of the plant '$plantName', including information about when to grow it, suitable atmosphere, and other relevant details, in $languageName."
                    _details.value = repository.getPlantDetails(prompt)
                } else {
                    _details.value = "Plant not found."
                }
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun identifyPlant(image: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val plantName = repository.identifyPlant(image)
                searchByName(plantName)
            } catch (t: Throwable) {
                _error.value = t.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
