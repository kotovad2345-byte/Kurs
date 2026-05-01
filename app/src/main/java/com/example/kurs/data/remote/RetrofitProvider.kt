package com.example.kurs.data.remote

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider {

    private const val API_NINJAS_BASE_URL = "https://api.api-ninjas.com/"
    private const val OPEN_METEO_BASE_URL = "https://api.open-meteo.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val gson = GsonBuilder()
        .create()

    private fun createRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val apiNinjasService: ApiNinjasService by lazy {
        createRetrofit(API_NINJAS_BASE_URL).create(ApiNinjasService::class.java)
    }

    val openMeteoService: OpenMeteoService by lazy {
        createRetrofit(OPEN_METEO_BASE_URL).create(OpenMeteoService::class.java)
    }
}
