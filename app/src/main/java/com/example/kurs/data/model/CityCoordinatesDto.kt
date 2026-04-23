package com.example.kurs.data.model

data class CityCoordinatesDto(
    val name: String,
    val country: String?,
    val state: String?,
    val latitude: Double,
    val longitude: Double
)
