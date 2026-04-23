package com.example.kurs.data.model

import com.google.gson.annotations.SerializedName

data class OpenMeteoHourlyDto(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>
)
