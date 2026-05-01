package com.example.kurs.model

data class SavedCity(
    val id: String,
    val name: String,
    val country: String,
    val region: String?,
    val latitude: Double,
    val longitude: Double
)
