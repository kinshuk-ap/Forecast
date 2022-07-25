package com.example.forecast.data.repository

import androidx.lifecycle.LiveData
import com.example.forecast.data.db.CurrentWeatherDao
import com.example.forecast.data.db.FutureWeatherDao
import com.example.forecast.data.db.WeatherLocationDao
import com.example.forecast.data.db.entity.Current
import com.example.forecast.data.db.entity.FutureWeatherEntry
import com.example.forecast.data.db.entity.Location
import com.example.forecast.data.network.FORECAST_DAYS_COUNT
import com.example.forecast.data.network.WeatherNetworkDataSource
import com.example.forecast.data.network.response.CurrentWeatherResponse
import com.example.forecast.data.network.response.FutureWeatherResponse
import com.example.forecast.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.ZonedDateTime

class ForecastRepositoryImpl(
    private val currentWeatherDao: CurrentWeatherDao,
    private val futureWeatherDao: FutureWeatherDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource,
    private val weatherLocationDao: WeatherLocationDao,
    private val locationProvider: LocationProvider
) : ForecastRepository {

    init {
        weatherNetworkDataSource.apply {
            downloadedCurrentWeather.observeForever { newCurrentWeather ->
                persistFetchedCurrentWeather(newCurrentWeather)
            }
            downloadedFutureWeather.observeForever { newFutureWeather ->
                persistFetchedFutureWeather(newFutureWeather)
            }
        }
    }
    override suspend fun getCurrentWeather(): LiveData<Current> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext currentWeatherDao.getCurrentWeather()
        }
    }

    override suspend fun getFutureWeatherList(startDate: LocalDate): LiveData<out List<FutureWeatherEntry>> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext futureWeatherDao.getSimpleWeatherForecasts(startDate)
        }
    }

    override suspend fun getWeatherLocation(): LiveData<Location> {
        return withContext(Dispatchers.IO) {
            return@withContext weatherLocationDao.getLocation()
        }
    }

    override suspend fun getFutureWeatherByDate(date: LocalDate): LiveData<out FutureWeatherEntry> {
        return withContext(Dispatchers.IO) {
            initWeatherData()
            return@withContext futureWeatherDao.getDetailedWeatherByDate(date)
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse) {
        GlobalScope.launch(Dispatchers.IO){
            currentWeatherDao.upsert(fetchedWeather.current)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }
    private fun persistFetchedFutureWeather(fetchedWeather: FutureWeatherResponse) {
        fun deleteOldForecastData() {
            val today = LocalDate.now()
            futureWeatherDao.deleteOldEntries(today)
        }
        GlobalScope.launch(Dispatchers.IO) {
            deleteOldForecastData()
            val futureWeatherList = fetchedWeather.futureWeatherEntries.entries
            futureWeatherDao.insert(futureWeatherList)
//            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData() {
        val lastWeatherLocation = weatherLocationDao.getLocationNonLive()

        if(lastWeatherLocation == null
            || locationProvider.hasLocationChanged(lastWeatherLocation)) {
            fetchCurrentWeather()
            fetchFutureWeather()
            return
        }
        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime)){
            fetchCurrentWeather()
        }
        if(isFutureWeatherNeeded()) {
            fetchFutureWeather()
        }
    }
    private suspend fun fetchCurrentWeather() {
        weatherNetworkDataSource.fetchCurrentWeather(
            locationProvider.getPreferredLocationString()
        )
    }
    private suspend fun fetchFutureWeather() {
        weatherNetworkDataSource.fetchFutureWeather(
            locationProvider.getPreferredLocationString()
        )
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean {
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
    private fun isFutureWeatherNeeded(): Boolean {
        val today = LocalDate.now()
        val futureWeatherCount = futureWeatherDao.countFutureWeather(today)
        return futureWeatherCount < FORECAST_DAYS_COUNT
    }
}