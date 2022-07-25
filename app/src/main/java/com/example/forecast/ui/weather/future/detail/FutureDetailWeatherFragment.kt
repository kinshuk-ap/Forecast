package com.example.forecast.ui.weather.future.detail

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.forecast.R
import com.example.forecast.data.db.Converters
import com.example.forecast.data.db.LocalDateConverter
import com.example.forecast.internal.DateNotFoundException
import com.example.forecast.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_future_detail_weather.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.factory
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureDetailWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactoryInstanceFactory
        : ((LocalDate) -> FutureDetailWeatherViewModelFactory) by factory()

    private lateinit var viewModel: FutureDetailWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_future_detail_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val safeArgs = arguments?.let { FutureDetailWeatherFragmentArgs.fromBundle(it) }
        val converter = LocalDateConverter()
        val date = converter.stringToDate(safeArgs?.dateString) ?: throw DateNotFoundException()

        viewModel = ViewModelProvider(
            this,
            viewModelFactoryInstanceFactory(date)
        )[FutureDetailWeatherViewModel::class.java]

        bindUI()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun bindUI() = launch(Dispatchers.Main){
        val futureWeather = viewModel.weather.await()

        futureWeather.observe(viewLifecycleOwner, Observer { weatherEntry ->
            if (weatherEntry == null) return@Observer

            updateDate(weatherEntry.date)

            textView_temperature.text = "${ weatherEntry.day.avgtempC }°C"
            textView_min_max_temperature.text = "Min: ${weatherEntry.day.mintempC}°C, Max: ${weatherEntry.day.maxtempC}°C"
            textView_condition.text = weatherEntry.day.condition.text
            textView_precipitation.text = "Precipitation: ${ weatherEntry.day.totalprecipMm }mm"
            textView_wind.text = "Max Wind Speed: ${ weatherEntry.day.maxwindKph }kph"
            textView_visibility.text = "Visibility: ${ weatherEntry.day.avgvisKm }Km"
            textView_uv.text = "UV: ${ weatherEntry.day.uv }"

            Glide.with(this@FutureDetailWeatherFragment)
                .load("http:" + weatherEntry.day.condition.icon)
                .into(imageView_condition_icon)
        })
    }
    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
    }

    private fun updateDate(date: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title =
            date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = null
    }
}