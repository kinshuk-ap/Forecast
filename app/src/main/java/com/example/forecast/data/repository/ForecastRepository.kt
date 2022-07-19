package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.db.entity.Current

interface ForecastRepository {
    suspend fun getCurrentWeather(): LiveData<Current>
}