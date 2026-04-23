package com.example.kurs.model

data class WeatherForecast(
    val timezone: String,
    val currentTemperature: Double,
    val currentWeatherDescription: String,
    val currentWindSpeed: Double,
    val hourlyForecasts: List<HourlyForecast>,
    val dailyForecasts: List<DailyForecast>
)
