package com.zzh.weatherkotlin

// ==========================================
// 1. 天气详情 (Day 和 Night 通用)
// ==========================================
data class WeatherDetail(
    val temperature: Int,
    val weather: String,
    val wind: String,
    val iconId: Int = 0
)

// ==========================================
// 2. 当天天气数据类
// ==========================================
data class CurrentWeather(
    val cityName: String,
    val date: String,
    val daytime: WeatherDetail,
    val night: WeatherDetail
)

// ==========================================
// 3. 未来预测数据类
// ==========================================
data class DailyForecast(
    val date: String,
    val dayOfWeek: String,
    val weather: String,
    val highTemp: Int,
    val lowTemp: Int
)

data class AmapResponse(val status: String, val forecasts: List<AmapForecastContainer>)
data class AmapForecastContainer(val city: String, val casts: List<AmapCast>)
data class AmapCast(val date: String, val week: String, val dayweather: String, val nightweather: String, val daytemp: String, val nighttemp: String, val daywind: String, val nightwind: String, val daypower: String, val nightpower: String)