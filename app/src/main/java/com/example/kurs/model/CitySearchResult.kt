package com.example.kurs.model

data class CitySearchResult(
    val id: String,
    val name: String,
    val country: String,
    val region: String?,
    val latitude: Double,
    val longitude: Double
)

fun CitySearchResult.toSavedCity(): SavedCity {
    return SavedCity(
        id = id,
        name = name,
        country = country,
        region = region,
        latitude = latitude,
        longitude = longitude
    )
}
