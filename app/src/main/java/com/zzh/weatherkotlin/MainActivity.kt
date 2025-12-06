package com.zzh.weatherkotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzh.weatherkotlin.adapter.ForecastAdapter
import com.zzh.weatherkotlin.DailyForecast
import okhttp3.*
import java.io.IOException
import com.google.gson.Gson
import com.zzh.weatherkotlin.network.WeatherManager

class MainActivity : AppCompatActivity() {

    // 容器
    private lateinit var layoutCityView: NestedScrollView
    private lateinit var layoutForecastView: NestedScrollView

    // 城市信息
    private lateinit var tvCity: TextView
    private lateinit var tvWeatherStatus: TextView
    private lateinit var tvCurrentTemp: TextView
    private lateinit var tvTempRange: TextView
    private lateinit var llForecastCity: TextView

    // 中间详情
    private lateinit var tvDayWeather: TextView
    private lateinit var tvDayWind: TextView
    private lateinit var tvDayTemp: TextView
    private lateinit var tvNightWeather: TextView
    private lateinit var tvNightWind: TextView
    private lateinit var tvNightTemp: TextView

    // 城市切换按钮
    private lateinit var btnBj: TextView
    private lateinit var btnSh: TextView
    private lateinit var btnGz: TextView
    private lateinit var btnSz: TextView

    // 底部导航
    private lateinit var navCity: LinearLayout
    private lateinit var navForecast: LinearLayout

    // 列表
    private lateinit var rvFutureWeather: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter
    private val forecastList = mutableListOf<DailyForecast>()

    // 当前选中的城市编号 (默认北京)
    private var currentCityCode = "110000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        initListeners()

        fetchWeatherData(currentCityCode)
        updateCityButtonState(btnBj)
    }

    private fun initViews() {
        // 容器
        layoutCityView = findViewById(R.id.layout_city_view)          //本日页面
        layoutForecastView = findViewById(R.id.layout_forecast_view)  //预测页面

        // 文本
        tvCity = findViewById(R.id.tv_city)
        llForecastCity = findViewById(R.id.ll_forecast_city)
        tvWeatherStatus = findViewById(R.id.tv_weather_status)
        tvCurrentTemp = findViewById(R.id.tv_current_temp)
        tvTempRange = findViewById(R.id.tv_temp_range)

        // 详情
        tvDayWeather = findViewById(R.id.tv_day_weather)
        tvDayWind = findViewById(R.id.tv_day_wind)
        tvDayTemp = findViewById(R.id.tv_day_temp)
        tvNightWeather = findViewById(R.id.tv_night_weather)
        tvNightWind = findViewById(R.id.tv_night_wind)
        tvNightTemp = findViewById(R.id.tv_night_temp)

        // 按钮
        btnBj = findViewById(R.id.btn_bj)
        btnSh = findViewById(R.id.btn_sh)
        btnGz = findViewById(R.id.btn_gz)
        btnSz = findViewById(R.id.btn_sz)

        // 导航
        navCity = findViewById(R.id.nav_city)
        navForecast = findViewById(R.id.nav_forecast)

        rvFutureWeather = findViewById(R.id.rv_future_weather)
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter(forecastList)
        rvFutureWeather.layoutManager = LinearLayoutManager(this)
        rvFutureWeather.adapter = forecastAdapter
    }

    private fun initListeners() {
        navCity.setOnClickListener {
            layoutCityView.visibility = View.VISIBLE
            layoutForecastView.visibility = View.GONE
        }

        navForecast.setOnClickListener {
            layoutCityView.visibility = View.GONE
            layoutForecastView.visibility = View.VISIBLE
        }

        // 北京: 110000
        btnBj.setOnClickListener {
            switchCity("110000", btnBj)
        }
        // 上海: 310000
        btnSh.setOnClickListener {
            switchCity("310000", btnSh)
        }
        // 广州: 440100
        btnGz.setOnClickListener {
            switchCity("440100", btnGz)
        }
        // 深圳: 440300
        btnSz.setOnClickListener {
            switchCity("440300", btnSz)
        }
    }
    private fun switchCity(cityCode: String, selectedBtn: TextView) {
        if (currentCityCode == cityCode) return

        currentCityCode = cityCode
        fetchWeatherData(cityCode)
        updateCityButtonState(selectedBtn)
    }

    private fun updateCityButtonState(selectedBtn: TextView) {
        val allButtons = listOf(btnBj, btnSh, btnGz, btnSz)
        allButtons.forEach { btn ->
            btn.setBackgroundResource(R.drawable.bg_btn_unselected)
        }
        selectedBtn.setBackgroundResource(R.drawable.bg_btn_selected)
    }

    private fun fetchWeatherData(cityCode: String) {

        WeatherManager.getCityWeather(cityCode, object : WeatherManager.WeatherCallback {
            override fun onSuccess(current: CurrentWeather, forecast: List<DailyForecast>) {
                runOnUiThread {
                    updateUI(current, forecast)
                }
            }
            override fun onError(msg: String) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateUI(current: CurrentWeather, forecast: List<DailyForecast>) {
        tvCity.text = current.cityName
        llForecastCity.text = current.cityName

        tvWeatherStatus.text = current.daytime.weather
        tvCurrentTemp.text = "${current.daytime.temperature}°"
        tvTempRange.text = "最高: ${current.daytime.temperature}°  最低: ${current.night.temperature}°"

        tvDayWeather.text = "天气：${current.daytime.weather}"
        tvDayTemp.text = "温度：${current.daytime.temperature}°"
        tvDayWind.text = "风向：${current.daytime.wind}"

        tvNightWeather.text = "天气：${current.night.weather}"
        tvNightTemp.text = "温度：${current.night.temperature}°"
        tvNightWind.text = "风向：${current.night.wind}"

        forecastList.clear()
        forecastList.addAll(forecast)
        forecastAdapter.notifyDataSetChanged()
    }
}

