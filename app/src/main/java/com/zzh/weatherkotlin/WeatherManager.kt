package com.zzh.weatherkotlin.network

import android.util.Log
import com.google.gson.Gson
import com.zzh.weatherkotlin.AmapResponse
import com.zzh.weatherkotlin.DailyForecast
import com.zzh.weatherkotlin.WeatherDetail
import com.zzh.weatherkotlin.CurrentWeather
import okhttp3.*
import java.io.IOException

object WeatherManager {
    private val client = OkHttpClient()
    private val gson = Gson()

    private const val AMAP_KEY = "1d2f0e97ab81187821624b70f9abbcf5"

    interface WeatherCallback {
        fun onSuccess(current: CurrentWeather, forecast: List<DailyForecast>)
        fun onError(msg: String)
    }

    fun getCityWeather(cityCode: String, callback: WeatherCallback) {
        val url = "https://restapi.amap.com/v3/weather/weatherInfo?city=$cityCode&key=$AMAP_KEY&extensions=all"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError("网络错误: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.onError("服务器错误")
                    return
                }

                val json = response.body?.string()
                if (json == null) {
                    callback.onError("数据为空")
                    return
                }

                try {
                    val amapResponse = gson.fromJson(json, AmapResponse::class.java)

                    if (amapResponse.status == "1" && amapResponse.forecasts.isNotEmpty()) {
                        val weatherData = amapResponse.forecasts[0]
                        val casts = weatherData.casts
                        if (casts.isNotEmpty()) {
                            val today = casts[0]
                            val currentWeather = CurrentWeather(
                                cityName = weatherData.city,
                                date = today.date,
                                daytime = WeatherDetail(
                                    temperature = today.daytemp.toIntOrNull() ?: 0,
                                    weather = today.dayweather,
                                    wind = today.daywind+ "风  " + today.daypower + "级"
                                ),
                                night = WeatherDetail(
                                    temperature = today.nighttemp.toIntOrNull() ?: 0,
                                    weather = today.nightweather,
                                    wind = today.nightwind + "风  " + today.nightpower + "级"
                                )
                            )
                            val forecastList = casts.map { cast ->
                                DailyForecast(
                                    date = cast.date.substring(5),
                                    dayOfWeek = getWeekStr(cast.week),
                                    weather = cast.dayweather,
                                    highTemp = cast.daytemp.toIntOrNull() ?: 0,
                                    lowTemp = cast.nighttemp.toIntOrNull() ?: 0
                                )
                            }
                            callback.onSuccess(currentWeather, forecastList)
                        }
                    } else {
                        callback.onError("API 返回无数据")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onError("数据解析异常")
                }
            }
        })
    }

    private fun getWeekStr(week: String): String {
        return when (week) {
            "1" -> "星期一"
            "2" -> "星期二"
            "3" -> "星期三"
            "4" -> "星期四"
            "5" -> "星期五"
            "6" -> "星期六"
            "7" -> "星期日"
            else -> "今天"
        }
    }
}