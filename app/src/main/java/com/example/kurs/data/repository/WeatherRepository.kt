package com.example.kurs.data.repository

import com.example.kurs.data.model.OpenMeteoResponseDto
import com.example.kurs.data.remote.ApiNinjasService
import com.example.kurs.data.remote.OpenMeteoService
import com.example.kurs.model.CitySearchResult
import com.example.kurs.model.DailyForecast
import com.example.kurs.model.HourlyForecast
import com.example.kurs.model.SavedCity
import com.example.kurs.model.WeatherForecast
import com.example.kurs.util.WeatherCodeMapper
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class WeatherRepository(
    private val apiNinjasService: ApiNinjasService,
    private val openMeteoService: OpenMeteoService
) {

    suspend fun searchCities(query: String, apiKey: String): Result<List<CitySearchResult>> = coroutineScope {
        runCatching {
            val trimmedQuery = query.trim()

            val openMeteoDeferred = async {
                runCatching { openMeteoService.searchCities(trimmedQuery, "ru", 20) }
            }

            val ninjasDeferred = async {
                if (apiKey.isNotBlank()) {
                    runCatching { apiNinjasService.searchCities(apiKey, trimmedQuery) }
                } else null
            }

            val openMeteoResults = openMeteoDeferred.await()
                .getOrNull()
                ?.results
                ?.map { city ->
                    CitySearchResult(
                        id = "om_${city.latitude}_${city.longitude}",
                        name = city.name,
                        country = city.country ?: "",
                        region = city.admin1 ?: "",
                        latitude = city.latitude,
                        longitude = city.longitude
                    )
                } ?: emptyList()

            val ninjasResults = ninjasDeferred.await()
                ?.getOrNull()
                ?.map { city ->
                    CitySearchResult(
                        id = "nj_${city.latitude}_${city.longitude}",
                        name = city.name,
                        country = city.country ?: "",
                        region = city.state ?: "",
                        latitude = city.latitude,
                        longitude = city.longitude
                    )
                } ?: emptyList()

            val finalResults = mutableListOf<CitySearchResult>()
            val all = openMeteoResults + ninjasResults

            for (city in all) {
                if (finalResults.none { it.isSameCity(city) }) {
                    finalResults.add(city)
                }
            }

            finalResults
        }
    }

    private fun String.normalizeCountry(): String {
        return when (lowercase()) {
            "ru", "russia", "россия" -> "russia"
            "ua", "ukraine", "украина" -> "ukraine"
            "by", "belarus", "беларусь" -> "belarus"
            else -> lowercase()
        }
    }

    private fun CitySearchResult.isSameCity(other: CitySearchResult): Boolean {
        val sameName = name.equals(other.name, ignoreCase = true)
        val sameCountry = country.normalizeCountry() == other.country.normalizeCountry()

        return sameName && sameCountry
    }

    suspend fun getWeatherForecast(city: SavedCity): Result<WeatherForecast> {
        return runCatching {
            openMeteoService.getWeatherForecast(
                latitude = city.latitude,
                longitude = city.longitude
            ).toWeatherForecast()
        }
    }

    private fun OpenMeteoResponseDto.toWeatherForecast(): WeatherForecast {
        val hourlyCount = listOf(
            hourly.time.size,
            hourly.temperature.size,
            hourly.weatherCode.size
        ).minOrNull() ?: 0

        val hourlyForecasts = buildList {
            repeat(hourlyCount) { index ->
                add(
                    HourlyForecast(
                        time = hourly.time[index],
                        temperature = hourly.temperature[index],
                        weatherDescription = WeatherCodeMapper.getDescription(hourly.weatherCode[index])
                    )
                )
            }
        }.take(24)

        val dailyCount = listOf(
            daily.time.size,
            daily.weatherCode.size,
            daily.temperatureMax.size,
            daily.temperatureMin.size,
            daily.windSpeedMax.size
        ).minOrNull() ?: 0

        val dailyForecasts = buildList {
            repeat(dailyCount) { index ->
                add(
                    DailyForecast(
                        date = daily.time[index],
                        weatherDescription = WeatherCodeMapper.getDescription(daily.weatherCode[index]),
                        maxTemperature = daily.temperatureMax[index],
                        minTemperature = daily.temperatureMin[index],
                        maxWindSpeed = daily.windSpeedMax[index]
                    )
                )
            }
        }

        return WeatherForecast(
            timezone = timezone,
            currentTemperature = current.temperature2m,
            currentWeatherDescription = WeatherCodeMapper.getDescription(current.weatherCode),
            currentWindSpeed = current.windSpeed10m,
            hourlyForecasts = hourlyForecasts,
            dailyForecasts = dailyForecasts
        )
    }
}
