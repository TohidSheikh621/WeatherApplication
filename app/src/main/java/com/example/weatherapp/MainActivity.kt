package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//784357f89391bcb125072cf96997ebc3
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.hide()
        fetchWeatherData("Nagpur")
        searchView()

    }

    private fun searchView() {
        val searchView = binding.searchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName: String) {


        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(ApiInterface::class.java)


        val response =
            retrofit.getWeatherData(cityName, "784357f89391bcb125072cf96997ebc3", "metric")

        response.enqueue(object : Callback<WeatherModel> {

            override fun onResponse(p0: Call<WeatherModel>, response: Response<WeatherModel>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val minTemp = responseBody.main.temp_min.toString()
                    val maxTemp = responseBody.main.temp_max.toString()
                    val humidity = responseBody.main.humidity
                    val sea_level = responseBody.main.pressure
                    val sunset = responseBody.sys.sunset.toLong()
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val windSpeed = responseBody.wind.speed
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"

                    binding.tempratureTv.text = "$temperature °C"
                    binding.minTempTv.text = "Min Temp : $minTemp °C"
                    binding.maxTempTv.text = "Max Temp : $maxTemp °C"
                    binding.humidityDataTv.text = "$humidity %"
                    binding.seaDataTv.text = "$sea_level hPa"
                    binding.sunsetDataTv.text = "${time(sunset)}"
                    binding.sunriseDataTv.text = "${time(sunrise)}"
                    binding.windSpeedDataTv.text = "$windSpeed m/s"
                    binding.weatherTv.text = condition
                    binding.conditionTv.text = condition
                    binding.dayTv.text = dayName(System.currentTimeMillis())
                    binding.dateTv.text = date()
                    binding.cityNameTv.text = "$cityName"

                    changeWeatherAccordingToConditions(condition)


                }
            }

            override fun onFailure(p0: Call<WeatherModel>, p1: Throwable) {
                Log.e("TAG", "Failed success $p1")
            }

        })
    }


    private fun changeWeatherAccordingToConditions(condition: String) {

        when (condition) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Mist", "Foggy", "Clouds", "Overcast" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()


    }

    private fun time(timeMillis: Long): String {
        val sdf = SimpleDateFormat("hh:MM", Locale.getDefault())
        return sdf.format(Date(timeMillis * 1000))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun dayName(timeMillis: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}

