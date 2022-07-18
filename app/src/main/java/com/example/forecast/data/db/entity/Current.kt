package com.example.forecast.data.db.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val CURRENT_WEATHER_ID = 0

@Entity(tableName = "current_weather")
data class  Current(
    val cloudcover: Int,
    val feelslike: Double,
    val humidity: Int,
    val precip: Double,
    val pressure: Double,
    val temperature: Double,
    val visibility: Int,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("weather_descriptions")
    val weatherDescriptions: List<String>,
    @SerializedName("weather_icons")
    val weatherIcons: List<String>,
    @SerializedName("wind_dir")
    val windDir: String,
    @SerializedName("wind_speed")
    val windSpeed: Double,

    @PrimaryKey(autoGenerate = false)
    val id: Int = CURRENT_WEATHER_ID
)