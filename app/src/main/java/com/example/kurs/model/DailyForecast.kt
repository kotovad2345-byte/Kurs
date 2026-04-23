package com.example.kurs.model

data class DailyForecast(
    val date: String,
    val weatherDescription: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val maxWindSpeed: Double
)
