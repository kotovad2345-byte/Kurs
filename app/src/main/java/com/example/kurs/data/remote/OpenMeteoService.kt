package com.example.kurs.data.remote

import com.example.kurs.data.model.OpenMeteoResponseDto
import com.example.kurs.data.model.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoService {

    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,wind_speed_10m_max",
        @Query("forecast_days") forecastDays: Int = 16,
        @Query("timezone") timezone: String = "auto"
    ): OpenMeteoResponseDto

    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("language") language: String = "ru",
        @Query("count") count: Int = 10
    ): GeocodingResponseDto
}
