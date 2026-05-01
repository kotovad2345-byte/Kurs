package com.example.kurs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kurs.data.repository.SavedCitiesRepository
import com.example.kurs.data.repository.WeatherRepository
import com.example.kurs.model.CitySearchResult
import com.example.kurs.model.SavedCity
import com.example.kurs.model.toSavedCity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val savedCitiesRepository: SavedCitiesRepository,
    private val weatherRepository: WeatherRepository,
    private val apiKey: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState = _uiState.asStateFlow()

    private var forecastJob: Job? = null

    init {
        observeSavedCities()
    }

    fun toggleCityList(open: Boolean) {
        _uiState.update { it.copy(isCityListOpen = open) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchError = null
            )
        }
    }

    fun searchCities() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isBlank()) {
            _uiState.update { it.copy(searchError = "Введите название города.") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearching = true,
                    searchError = null,
                    searchResults = emptyList()
                )
            }

            weatherRepository.searchCities(query, apiKey)
                .onSuccess { cities ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResults = cities,
                            searchError = if (cities.isEmpty()) "По запросу ничего не найдено." else null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isSearching = false,
                            searchResults = emptyList(),
                            searchError = throwable.toUserMessage()
                        )
                    }
                }
        }
    }

    fun addCity(city: CitySearchResult) {
        viewModelScope.launch {
            val savedCity = city.toSavedCity()
            savedCitiesRepository.addCity(savedCity)
            _uiState.update {
                it.copy(
                    selectedCityId = savedCity.id,
                    forecast = null,
                    forecastError = null,
                    searchQuery = "",
                    searchResults = emptyList(),
                    searchError = null,
                    isCityListOpen = false
                )
            }
            loadForecast(savedCity)
        }
    }

    fun selectCity(cityId: String) {
        val city = _uiState.value.savedCities.firstOrNull { it.id == cityId } ?: return
        _uiState.update {
            it.copy(
                selectedCityId = city.id,
                forecast = null,
                forecastError = null,
                isCityListOpen = false
            )
        }
        loadForecast(city)
    }

    fun removeCity(cityId: String) {
        viewModelScope.launch {
            savedCitiesRepository.removeCity(cityId)
            _uiState.update {
                if (it.selectedCityId == cityId) {
                    it.copy(selectedCityId = null, forecast = null)
                } else {
                    it
                }
            }
        }
    }

    fun refreshSelectedCity() {
        val selectedCity = _uiState.value.selectedCity ?: return
        loadForecast(selectedCity)
    }

    private fun observeSavedCities() {
        viewModelScope.launch {
            savedCitiesRepository.savedCities.collectLatest { cities ->
                val previousState = _uiState.value

                val nextSelectedCityId = when {
                    cities.isEmpty() -> null
                    previousState.selectedCityId != null &&
                            cities.any { it.id == previousState.selectedCityId } -> {
                        previousState.selectedCityId
                    }
                    else -> cities.first().id
                }

                _uiState.update {
                    it.copy(
                        savedCities = cities,
                        selectedCityId = nextSelectedCityId
                    )
                }

                if (nextSelectedCityId != null && _uiState.value.forecast == null) {
                    cities.find { it.id == nextSelectedCityId }?.let { loadForecast(it) }
                }
            }
        }
    }

    private fun loadForecast(city: SavedCity) {
        forecastJob?.cancel()
        forecastJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoadingForecast = true,
                    forecastError = null
                )
            }

            weatherRepository.getWeatherForecast(city)
                .onSuccess { forecast ->
                    _uiState.update { state ->
                        if (state.selectedCityId != city.id) state
                        else state.copy(
                            isLoadingForecast = false,
                            forecast = forecast,
                            forecastError = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { state ->
                        if (state.selectedCityId != city.id) state
                        else state.copy(
                            isLoadingForecast = false,
                            forecastError = throwable.toUserMessage()
                        )
                    }
                }
        }
    }

    private fun Throwable.toUserMessage(): String {
        val message = message?.trim()
        if (message.isNullOrBlank()) return "Ошибка сети. Проверьте интернет."
        return "Не удалось получить данные: $message"
    }
}