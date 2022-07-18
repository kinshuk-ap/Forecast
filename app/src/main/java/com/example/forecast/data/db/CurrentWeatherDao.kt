package com.example.forecast.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecast.data.db.entity.CURRENT_WEATHER_ID
import com.example.forecast.data.db.entity.Current

@Dao
interface CurrentWeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(current: Current)

    @Query("SELECT * FROM current_weather WHERE id=$CURRENT_WEATHER_ID")
    fun getCurrentWeather(): LiveData<Current>
}