package com.example.kurs.data.remote

import com.example.kurs.data.model.CityCoordinatesDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiNinjasService {

    @GET("v1/city")
    suspend fun searchCities(
        @Header("X-Api-Key") apiKey: String,
        @Query("name") cityName: String,
        @Query("limit") limit: Int = 5
    ): List<CityCoordinatesDto>
}
