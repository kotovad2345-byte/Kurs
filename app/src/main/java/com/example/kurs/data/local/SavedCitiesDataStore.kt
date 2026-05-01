package com.example.kurs.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.kurs.model.SavedCity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private const val WEATHER_PREFERENCES = "weather_preferences"
private val Context.savedCitiesDataStore by preferencesDataStore(name = WEATHER_PREFERENCES)

class SavedCitiesDataStore(private val context: Context) {

    private val gson = Gson()
    private val savedCitiesKey = stringPreferencesKey("saved_cities")

    val savedCities: Flow<List<SavedCity>> = context.savedCitiesDataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences -> decodeCities(preferences[savedCitiesKey]) }

    suspend fun updateCities(cities: List<SavedCity>) {
        context.savedCitiesDataStore.edit { preferences ->
            if (cities.isEmpty()) {
                preferences.remove(savedCitiesKey)
            } else {
                preferences[savedCitiesKey] = gson.toJson(cities)
            }
        }
    }

    private fun decodeCities(json: String?): List<SavedCity> {
        if (json.isNullOrBlank()) {
            return emptyList()
        }

        val listType = object : TypeToken<List<SavedCity>>() {}.type
        return gson.fromJson(json, listType) ?: emptyList()
    }
}
