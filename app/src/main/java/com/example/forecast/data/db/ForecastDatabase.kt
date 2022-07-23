package com.example.forecast.data.db

import android.content.Context
import androidx.room.*
import com.example.forecast.data.db.entity.Current
import com.example.forecast.data.db.entity.FutureWeatherEntry
import com.example.forecast.data.db.entity.Location

@Database(
    entities = [Current::class, FutureWeatherEntry::class, Location::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ForecastDatabase: RoomDatabase() {
    abstract fun currentWeatherDao() : CurrentWeatherDao
    abstract fun weatherLocationDao(): WeatherLocationDao
    abstract fun futureWeatherDao()  : FutureWeatherDao

    companion object {
        @Volatile
        private var instance: ForecastDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                ForecastDatabase::class.java, "forecast.db")
                .build()
    }
}