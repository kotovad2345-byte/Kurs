package com.example.kurs

import android.app.Application
import com.example.kurs.data.local.SavedCitiesDataStore
import com.example.kurs.data.remote.RetrofitProvider
import com.example.kurs.data.repository.SavedCitiesRepository
import com.example.kurs.data.repository.WeatherRepository

class WeatherApplication : Application() {

    val savedCitiesRepository: SavedCitiesRepository by lazy {
        SavedCitiesRepository(SavedCitiesDataStore(applicationContext))
    }

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(
            apiNinjasService = RetrofitProvider.apiNinjasService,
            openMeteoService = RetrofitProvider.openMeteoService
        )
    }
}
