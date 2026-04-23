package com.example.kurs.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kurs.data.repository.SavedCitiesRepository
import com.example.kurs.data.repository.WeatherRepository

class WeatherViewModelFactory(
    private val savedCitiesRepository: SavedCitiesRepository,
    private val weatherRepository: WeatherRepository,
    private val apiKey: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(
                savedCitiesRepository = savedCitiesRepository,
                weatherRepository = weatherRepository,
                apiKey = apiKey
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
