package com.example.forecast.ui.weather.current

import androidx.lifecycle.ViewModel
import com.example.forecast.data.repository.ForecastRepository
import com.example.forecast.internal.lazyDeferrd

class CurrentWeatherViewModel(private val forecastRepository: ForecastRepository) : ViewModel() {
    val weather by lazyDeferrd {
        forecastRepository.getCurrentWeather()
    }
    val weatherLocation by lazyDeferrd {
        forecastRepository.getWeatherLocation()
    }
}