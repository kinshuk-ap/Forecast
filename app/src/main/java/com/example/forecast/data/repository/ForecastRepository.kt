package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.db.entity.Current
import com.example.forecast.data.db.entity.FutureWeatherEntry
import com.example.forecast.data.db.entity.Location
import org.threeten.bp.LocalDate

interface ForecastRepository {
    suspend fun getCurrentWeather(): LiveData<Current>
    suspend fun getFutureWeatherList(startDate: LocalDate): LiveData<out List<FutureWeatherEntry>>
    suspend fun getFutureWeatherByDate(date: LocalDate): LiveData<out FutureWeatherEntry>
    suspend fun getWeatherLocation(): LiveData<Location>
}