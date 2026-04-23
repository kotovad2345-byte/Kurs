package com.example.kurs.ui

import com.example.kurs.model.CitySearchResult
import com.example.kurs.model.SavedCity
import com.example.kurs.model.WeatherForecast

data class WeatherUiState(
    val savedCities: List<SavedCity> = emptyList(),
    val selectedCityId: String? = null,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<CitySearchResult> = emptyList(),
    val searchError: String? = null,
    val isLoadingForecast: Boolean = false,
    val forecast: WeatherForecast? = null,
    val forecastError: String? = null,
    val popularCities: List<CitySearchResult> = DefaultPopularCities,
    val isCityListOpen: Boolean = false
) {
    val selectedCity: SavedCity?
        get() = savedCities.firstOrNull { it.id == selectedCityId }
}

val DefaultPopularCities = listOf(
    CitySearchResult("moscow", "Москва", "RU", null, 55.7558, 37.6173),
    CitySearchResult("spb", "Санкт-Петербург", "RU", null, 59.9343, 30.3351),
    CitySearchResult("novosibirsk", "Новосибирск", "RU", null, 55.0084, 82.9357),
    CitySearchResult("ekaterinburg", "Екатеринбург", "RU", null, 56.8389, 60.6057),
    CitySearchResult("kazan", "Казань", "RU", null, 55.7887, 49.1221),
    CitySearchResult("nizhny_novgorod", "Нижний Новгород", "RU", null, 56.3269, 44.0059),
    CitySearchResult("samara", "Самара", "RU", null, 53.2001, 50.15),
    CitySearchResult("london", "London", "GB", null, 51.5074, -0.1278),
    CitySearchResult("new_york", "New York", "US", null, 40.7128, -74.0060)
)
