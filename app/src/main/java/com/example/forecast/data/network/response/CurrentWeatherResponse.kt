package com.example.forecast.data.network.response

import com.example.forecast.data.db.entity.Current
import com.example.forecast.data.db.entity.Location
import com.example.forecast.data.db.entity.Request


data class CurrentWeatherResponse(
    val current: Current,
    val location: Location,
    val request: Request
)