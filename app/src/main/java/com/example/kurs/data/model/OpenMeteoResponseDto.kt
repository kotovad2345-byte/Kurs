package com.example.kurs.data.model

data class OpenMeteoResponseDto(
    val timezone: String,
    val current: OpenMeteoCurrentDto,
    val hourly: OpenMeteoHourlyDto,
    val daily: OpenMeteoDailyDto
)
