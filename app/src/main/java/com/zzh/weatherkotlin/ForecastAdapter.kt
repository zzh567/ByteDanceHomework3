package com.zzh.weatherkotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zzh.weatherkotlin.R
import com.zzh.weatherkotlin.DailyForecast

class ForecastAdapter(private val forecastList: List<DailyForecast>) :
    RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    inner class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayOfWeek: TextView = view.findViewById(R.id.tv_day_of_week)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvWeather: TextView = view.findViewById(R.id.tv_weather)
        val tvTempRange: TextView = view.findViewById(R.id.tv_temp_range)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val data = forecastList[position]

        // 绑定文本数据
        holder.tvDayOfWeek.text = data.dayOfWeek // e.g. "星期五"
        holder.tvDate.text = data.date           // e.g. "01-12"
        holder.tvWeather.text = data.weather     // e.g. "多云"

        // 拼接温度字符串 (e.g. "24° 18°")
        holder.tvTempRange.text = "${data.highTemp}° ${data.lowTemp}°"

    }

    override fun getItemCount() = forecastList.size
}