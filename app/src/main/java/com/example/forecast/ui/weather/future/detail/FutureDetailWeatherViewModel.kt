package com.example.forecast.ui.weather.future.detail

import androidx.lifecycle.ViewModel
import com.example.forecast.data.repository.ForecastRepository
import com.example.forecast.internal.lazyDeferrd
import org.threeten.bp.LocalDate

class FutureDetailWeatherViewModel(
    private val detailDate: LocalDate,
    private val forecastRepository: ForecastRepository
) : ViewModel() {
    val weather by lazyDeferrd {
        forecastRepository.getFutureWeatherByDate(detailDate)
    }

}