package com.example.kurs.data.repository

import com.example.kurs.data.local.SavedCitiesDataStore
import com.example.kurs.model.SavedCity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SavedCitiesRepository(private val savedCitiesDataStore: SavedCitiesDataStore) {

    val savedCities: Flow<List<SavedCity>> = savedCitiesDataStore.savedCities

    suspend fun addCity(city: SavedCity) {
        val currentCities = savedCities.first()
        if (currentCities.any { it.id == city.id }) {
            return
        }

        val updatedCities = (currentCities + city)
            .sortedBy { it.name.lowercase() }

        savedCitiesDataStore.updateCities(updatedCities)
    }

    suspend fun removeCity(cityId: String) {
        val updatedCities = savedCities.first().filterNot { it.id == cityId }
        savedCitiesDataStore.updateCities(updatedCities)
    }
}
