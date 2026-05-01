package com.example.kurs.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kurs.model.CitySearchResult
import com.example.kurs.model.DailyForecast
import com.example.kurs.model.HourlyForecast
import com.example.kurs.model.SavedCity
import com.example.kurs.model.WeatherForecast
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    uiState: WeatherUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddCityClick: (CitySearchResult) -> Unit,
    onSelectCityClick: (String) -> Unit,
    onDeleteCityClick: (String) -> Unit,
    onRefreshClick: () -> Unit,
    onToggleCityList: (Boolean) -> Unit
) {
    if (uiState.isCityListOpen) {
        CityListScreen(
            uiState = uiState,
            onSearchQueryChange = onSearchQueryChange,
            onSearchClick = onSearchClick,
            onAddCityClick = onAddCityClick,
            onSelectCityClick = onSelectCityClick,
            onDeleteCityClick = onDeleteCityClick,
            onClose = { onToggleCityList(false) }
        )
    } else {
        MainWeatherScreen(
            uiState = uiState,
            onRefreshClick = onRefreshClick,
            onOpenCityList = { onToggleCityList(true) }
        )
    }
}

@Composable
private fun MainWeatherScreen(
    uiState: WeatherUiState,
    onRefreshClick: () -> Unit,
    onOpenCityList: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF4A90E2), Color(0xFF87CEEB))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                BottomBar(onOpenCityList = onOpenCityList, onRefreshClick = onRefreshClick)
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(top = 60.dp, bottom = 20.dp, start = 20.dp, end = 20.dp)
            ) {
                val selectedCity = uiState.selectedCity
                val forecast = uiState.forecast

                if (selectedCity == null) {
                    item {
                        Text(
                            "Добавьте город",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                } else {
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = selectedCity.name ?: "",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Normal
                            )
                            if (forecast != null) {
                                Text(
                                    text = "${forecast.currentTemperature.roundToInt()}°",
                                    color = Color.White,
                                    fontSize = 80.sp,
                                    fontWeight = FontWeight.Thin
                                )
                                Text(
                                    text = forecast.currentWeatherDescription,
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }

                    if (uiState.isLoadingForecast) {
                        item { Text("Обновление...", color = Color.White) }
                    }

                    if (uiState.forecastError != null) {
                        item { Text(uiState.forecastError, color = Color.Red) }
                    }

                    if (forecast != null) {
                        item {
                            HourlyForecastCard(forecast.hourlyForecasts)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        item {
                            ForecastCard(forecast.dailyForecasts)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyForecastCard(hourlyForecasts: List<HourlyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "ПОЧАСОВОЙ ПРОГНОЗ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(hourlyForecasts) { hour ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.width(72.dp)
                    ) {
                        Text(
                            text = formatHourlyTime(hour.time),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${hour.temperature.roundToInt()}°",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = hour.weatherDescription,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastCard(dailyForecasts: List<DailyForecast>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "ПРОГНОЗ НА 16 ДНЕЙ",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            dailyForecasts.forEachIndexed { index, day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (index == 0) "Сегодня" else formatForecastDate(day.date),
                        color = Color.White,
                        modifier = Modifier.width(80.dp),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = day.weatherDescription,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${day.minTemperature.roundToInt()}°",
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.width(30.dp),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "${day.maxTemperature.roundToInt()}°",
                        color = Color.White,
                        modifier = Modifier.width(40.dp),
                        textAlign = TextAlign.End
                    )
                }
                if (index < dailyForecasts.size - 1) {
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                }
            }
        }
    }
}

@Composable
private fun CityListScreen(
    uiState: WeatherUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onAddCityClick: (CitySearchResult) -> Unit,
    onSelectCityClick: (String) -> Unit,
    onDeleteCityClick: (String) -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding().padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Погода", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск города") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text,
                        autoCorrectEnabled = true
                    ),
                    keyboardActions = KeyboardActions(onSearch = { onSearchClick() })
                )
                if (uiState.searchQuery.isNotEmpty()) {
                    Button(onClick = onSearchClick, modifier = Modifier.align(Alignment.End).padding(top = 8.dp)) {
                        Text("Найти")
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.searchResults.isNotEmpty()) {
                item { Text("Результаты поиска", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp)) }
                items(uiState.searchResults) { city ->
                    CitySearchItem(city, onAddClick = { onAddCityClick(city) })
                }
                item { HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp)) }
            }

            item {
                PopularCitiesSection(
                    popularCities = uiState.popularCities,
                    savedCities = uiState.savedCities,
                    onCityClick = onAddCityClick
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { Text("Ваши города", fontWeight = FontWeight.Bold) }

            items(uiState.savedCities) { city ->
                CitySavedItem(
                    city = city,
                    isSelected = city.id == uiState.selectedCityId,
                    onSelect = { onSelectCityClick(city.id) },
                    onDelete = { onDeleteCityClick(city.id) }
                )
            }
        }
    }
}

@Composable
private fun CitySavedItem(city: SavedCity, isSelected: Boolean, onSelect: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF4A90E2) else Color(0xFFF0F0F0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    city.name ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else Color.Black
                )
                Text(
                    text = listOfNotNull(
                        city.region?.takeIf { it.isNotBlank() },
                        city.country?.takeIf { it.isNotBlank() }
                    ).joinToString(", "),
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
private fun CitySearchItem(city: CitySearchResult, onAddClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAddClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(city.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = listOfNotNull(
                    city.region?.takeIf { it.isNotBlank() },
                    city.country?.takeIf { it.isNotBlank() }
                ).joinToString(", "),
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PopularCitiesSection(
    popularCities: List<CitySearchResult>,
    savedCities: List<SavedCity>,
    onCityClick: (CitySearchResult) -> Unit
) {
    Column {
        Text("Быстрый выбор", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (city in popularCities) {
                val isAdded = savedCities.any {
                    val latDiff = kotlin.math.abs(it.latitude - city.latitude)
                    val lonDiff = kotlin.math.abs(it.longitude - city.longitude)
                    latDiff < 0.01 && lonDiff < 0.01
                }
                AssistChip(
                    onClick = { if (!isAdded) onCityClick(city) },
                    label = { Text(city.name ?: "") },
                    enabled = !isAdded,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.White,
                        labelColor = Color.Black
                    ),
                    border = BorderStroke(1.dp, Color.Black),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
private fun BottomBar(onOpenCityList: () -> Unit, onRefreshClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onRefreshClick) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        IconButton(onClick = onOpenCityList) {
            Icon(Icons.Default.List, contentDescription = "Cities", tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

private fun formatForecastDate(date: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formatter = SimpleDateFormat("EE", Locale.forLanguageTag("ru-RU"))
    return runCatching {
        parser.parse(date)?.let(formatter::format) ?: date
    }.getOrDefault(date).replaceFirstChar { it.uppercase() }
}

private fun formatHourlyTime(time: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
    val formatter = SimpleDateFormat("HH:00", Locale.getDefault())
    return runCatching {
        parser.parse(time)?.let(formatter::format) ?: time
    }.getOrDefault(time)
}
