package com.example.kurs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kurs.ui.WeatherScreen
import com.example.kurs.ui.WeatherViewModel
import com.example.kurs.ui.WeatherViewModelFactory
import com.example.kurs.ui.theme.KursTheme

class MainActivity : ComponentActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels {
        val application = application as WeatherApplication
        WeatherViewModelFactory(
            savedCitiesRepository = application.savedCitiesRepository,
            weatherRepository = application.weatherRepository,
            apiKey = BuildConfig.API_NINJAS_KEY
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KursTheme {
                val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen(
                        uiState = uiState,
                        onSearchQueryChange = weatherViewModel::onSearchQueryChange,
                        onSearchClick = weatherViewModel::searchCities,
                        onAddCityClick = weatherViewModel::addCity,
                        onSelectCityClick = weatherViewModel::selectCity,
                        onDeleteCityClick = weatherViewModel::removeCity,
                        onRefreshClick = weatherViewModel::refreshSelectedCity,
                        onToggleCityList = weatherViewModel::toggleCityList
                    )
                }
            }
        }
    }
}
