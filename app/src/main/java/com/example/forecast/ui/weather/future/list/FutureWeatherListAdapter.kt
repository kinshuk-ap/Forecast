package com.example.forecast.ui.weather.future.list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.runtime.currentRecomposeScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.forecast.R
import com.example.forecast.data.db.LocalDateConverter
import com.example.forecast.data.db.entity.FutureWeatherEntry
import kotlinx.android.synthetic.main.fragment_current_weather.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

class FutureWeatherListAdapter(
    private val weatherEntries: List<FutureWeatherEntry>
    ): RecyclerView.Adapter<FutureViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_future_weather,
            parent,
           false)
        return FutureViewHolder(view)
    }

    override fun onBindViewHolder(holder: FutureViewHolder, position: Int) {
        val currentItem = weatherEntries[position]
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val imgUrl = "http:" + currentItem.day.condition.icon

        holder.date.text = currentItem.date.format(dtFormatter)
        holder.temperature.text = "${currentItem.day.avgtempC}°C"
        holder.condition.text = currentItem.day.condition.text
        Glide.with(holder.itemView.context)
            .load(imgUrl)
            .into(holder.conditionImg)

        holder.itemView.setOnClickListener{
            Toast.makeText(holder.itemView.context, "clicked", Toast.LENGTH_LONG).show()
            ( currentItem as? FutureWeatherEntry)?.let {
                showWeatherDetail(it.date, holder.itemView)
            }
        }
    }
    override fun getItemCount(): Int {
        return weatherEntries.size
    }
    private fun showWeatherDetail(date: String, view: View) {
        val actionDetail = FutureListWeatherFragmentDirections.actionDetail(date)
        Navigation.findNavController(view).navigate(actionDetail)
    }
}
class FutureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val temperature: TextView = itemView.findViewById(R.id.textView_temperature)
    val date: TextView = itemView.findViewById(R.id.textView_date)
    val condition: TextView = itemView.findViewById(R.id.textView_condition)
    val conditionImg: ImageView = itemView.findViewById(R.id.imageView_condition_icon)
}