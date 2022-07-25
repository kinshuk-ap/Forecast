package com.example.forecast

import android.app.Application
import android.content.Context
import com.example.forecast.data.db.ForecastDatabase
import com.example.forecast.data.network.*
import com.example.forecast.data.provider.LocationProvider
import com.example.forecast.data.provider.LocationProviderImpl
import com.example.forecast.data.repository.ForecastRepository
import com.example.forecast.data.repository.ForecastRepositoryImpl
import com.example.forecast.ui.weather.current.CurrentWeatherViewModel
import com.example.forecast.ui.weather.current.CurrentWeatherViewModelFactory
import com.example.forecast.ui.weather.future.detail.FutureDetailWeatherViewModelFactory
import com.example.forecast.ui.weather.future.list.FutureListWeatherViewModel
import com.example.forecast.ui.weather.future.list.FutureListWeatherViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import org.threeten.bp.LocalDate

class ForecastApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(app = this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().futureWeatherDao() }
        bind() from singleton { instance<ForecastDatabase>().weatherLocationDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApixuWeatherApiService(instance()) }
        bind() from singleton { ApixuFutureWeatherApiService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance(), instance()) }
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }
        bind<LocationProvider>() with singleton { LocationProviderImpl(instance(), instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(), instance(), instance(), instance(), instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance()) }
        bind() from provider { FutureListWeatherViewModelFactory(instance()) }
        bind() from factory { detailDate: LocalDate -> FutureDetailWeatherViewModelFactory(detailDate, instance())}
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}