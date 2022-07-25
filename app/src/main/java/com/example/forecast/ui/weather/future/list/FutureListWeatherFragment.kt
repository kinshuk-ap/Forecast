package com.example.forecast.ui.weather.future.list

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forecast.R
import com.example.forecast.data.db.entity.FutureWeatherEntry
import com.example.forecast.ui.base.ScopedFragment
import kotlinx.android.synthetic.main.fragment_future_list_weather.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class FutureListWeatherFragment : ScopedFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: FutureListWeatherViewModelFactory by instance()

    private lateinit var viewModel: FutureListWeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, viewModelFactory)[FutureListWeatherViewModel::class.java]
        bindUI()
        return inflater.inflate(R.layout.fragment_future_list_weather, container, false)
    }

    private fun bindUI() = launch(Dispatchers.Main) {
        val futureWeatherEntries = viewModel.weatherEntries.await()
        val weatherLocation = viewModel.weatherLocation.await()

        weatherLocation.observe(viewLifecycleOwner, Observer { location ->
            if(location == null) return@Observer
            updateLocation(location.name)
        })
        futureWeatherEntries.observe(viewLifecycleOwner, Observer { weatherEntries ->
            if (weatherEntries == null) return@Observer

            group_loading.visibility = View.GONE

            initRecyclerView(weatherEntries.toFutureWeatherItems())
        })
    }

    private fun updateLocation(location: String) {
        (activity as AppCompatActivity)?.supportActionBar?.title    = location
        (activity as AppCompatActivity)?.supportActionBar?.subtitle = "Next Week"
    }

    private fun List<FutureWeatherEntry>.toFutureWeatherItems(): List<FutureWeatherEntry> {
        return this.map {
            FutureWeatherEntry(it.id, it.date, it.day)
        }
    }
    private fun initRecyclerView(weatherEntries: List<FutureWeatherEntry>) {
        recyclerView.layoutManager = LinearLayoutManager(this@FutureListWeatherFragment.context)
        val adapter = FutureWeatherListAdapter(weatherEntries)


        recyclerView.adapter = adapter
    }
}