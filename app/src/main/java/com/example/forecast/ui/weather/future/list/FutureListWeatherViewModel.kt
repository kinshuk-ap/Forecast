package com.example.forecast.ui.weather.future.list

import androidx.lifecycle.ViewModel
import com.example.forecast.data.repository.ForecastRepository
import com.example.forecast.internal.lazyDeferrd
import org.threeten.bp.LocalDate

class FutureListWeatherViewModel(
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    val weatherEntries by lazyDeferrd {
        forecastRepository.getFutureWeatherList(LocalDate.now())
    }
    val weatherLocation by lazyDeferrd {
        forecastRepository.getWeatherLocation()
    }
}