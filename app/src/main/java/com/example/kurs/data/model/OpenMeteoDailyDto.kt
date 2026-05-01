package com.example.kurs.data.model

import com.google.gson.annotations.SerializedName

data class OpenMeteoDailyDto(
    val time: List<String>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
    @SerializedName("wind_speed_10m_max") val windSpeedMax: List<Double>
)
