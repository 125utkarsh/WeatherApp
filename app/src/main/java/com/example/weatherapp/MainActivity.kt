package com.example.weatherapp

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
//import com.example.weatherapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Lucknow")
        Searchcity()
    }

    private fun Searchcity() {
        val searchView = binding.searching
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)
        val response = retrofit.getWeatherData(cityName, "63591e99ca2736b0971906f21aa2c127", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val temperature = responseBody.main.temp.toString()
                        val humidity = responseBody.main.humidity
                        val windSpeed = responseBody.wind.speed
                        val sunrise = responseBody.sys.sunrise.toLong()
                        val sunset = responseBody.sys.sunset.toLong()
                        val sealevel = responseBody.main.pressure
                        val condition = responseBody.weather.firstOrNull()?.main?: "unknown"

                        binding.temp.text = "$temperature °C"
                        binding.Condition.text = condition
                        binding.Humidity.text = "$humidity %"
                        binding.Windspeed.text = "$windSpeed m/s"
                        binding.Sunrise.text = "${time(sunrise)}"
                        binding.Sunset.text = "${time(sunset)}"
                        binding.Sealevel.text = "$sealevel hPa"
                        binding.Day.text = dayname(System.currentTimeMillis())
                        binding.Date.text = date()
                        binding.Location.text = "$cityName"
                       // Log.d(TAG, "onResponse: Temperature is $temperature")

                        changeImage(condition)
                    }
                } else {
                    // Handle error response
                    Log.e("TAG", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                // Handle failure
                Log.e("TAG", "onFailure: ${t.message}")
            }
        })
    }

    private fun changeImage(conditions: String) {
        when (conditions) {
            "Clear Sky","Clear","Sunny" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.imageView.setImageResource(R.drawable.sunny)
            }

            "Partly Clouds","Clouds","Overcast","Mist","Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.cloud)
                binding.imageView.setImageResource(R.drawable.cloud_black)
            }

            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Thunderstorm" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.imageView.setImageResource(R.drawable.rain)
            }

            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.imageView.setImageResource(R.drawable.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.imageView.setImageResource(R.drawable.sunny)
            }

        }
    }

    fun dayname(timestamp: Long): String{
        val day = SimpleDateFormat("EEEE" , Locale.getDefault())
        return day.format((Date()))
    }

    private fun date(): String{
        val day = SimpleDateFormat("dd MMMM" , Locale.getDefault())
        return day.format((Date()))
    }

    private fun time(timestamp: Long): String{
        val time = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return time.format((Date(timestamp*1000)))
    }

}





