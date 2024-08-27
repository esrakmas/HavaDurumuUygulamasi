package com.example.havadurumuuygulamasi

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.havadurumuuygulamasi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val apiKey = "231d1fec17dba91b5baf5a40dfff0cfb"
    private val cityName = "ankara"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData(apiKey, cityName)


    }


    //apiden veri çekme fonksiyonu
    private fun fetchWeatherData(apiKey: String, cityName: String) {

        WeatherService.weatherApi.getCurrentWeather(cityName,apiKey).enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {

                Log.d("kontrol",response.toString())

                if (response.isSuccessful) {
                    Log.d("kontrol",response.body()?.name.toString())

                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                //TODO: error handler
                Log.d("kontrol","fail")
                }
        })
    }
}