package com.example.havadurumuuygulamasi

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.havadurumuuygulamasi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response




class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val apiKey = "231d1fec17dba91b5baf5a40dfff0cfb"


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

      /*  binding.btnGetWeather.setOnClickListener {
            val cityName = binding.etCityInput.text.toString()
            if (cityName.isNotEmpty()) {
                fetchWeatherData(apiKey, cityName)
            }
        }*/

        // SearchView için listener ayarlama
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val cityName = query ?: return false
                fetchWeatherData(apiKey, cityName)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //listview gelince işte alttan liste çıksın falan
                return true
            }
        })


    }



    //apiden veri çekme fonksiyonu
    private fun fetchWeatherData(apiKey: String, cityName: String) {
        WeatherService.weatherApi.getCurrentWeather(cityName, apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                //enqueue Metodu: Retrofit kütüphanesinde, enqueue metodu asenkron bir ağ çağrısı yapar.
                // Bu çağrı, çalıştırıldığında sunucuya bir istek gönderir
                // ve yanıt alındığında (bu başarı veya başarısızlık olabilir)
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    Log.d("kontrol", weatherResponse?.name.toString())

                    binding.tvCityName.text = weatherResponse?.name ?: "Şehir Bilinmiyor"

                    // Günün zamanına göre arka planı belirle
                    val iconCode = weatherResponse?.weather?.get(0)?.icon ?: "01d"
                    val isDayTime = iconCode.endsWith("d")

                    val backgroundDrawable = if (isDayTime) {
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.day_bg)
                    } else {
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.night_bg)
                    }


                    // Hava durumu ikonu belirleme ve imageView'e set etme

                    Log.d("IconCode", "Gelen iconCode: $iconCode")

                   val iconResId = when (iconCode) {

                        "01d" -> R.drawable.clear_day
                        "01n" -> R.drawable.clear_night
                        "02d" -> R.drawable.cloudy_day
                        "02n" -> R.drawable.cloudy_night
                        "03d","03n"-> R.drawable.cloudy
                        "04d","04n"-> R.drawable.cloudy
                        "09d" -> R.drawable.rainy_day
                        "09n" -> R.drawable.rainy_night
                        "10d" -> R.drawable.rainy3_day
                        "10n" -> R.drawable.rainy3_night
                        "11d" -> R.drawable.thunderstorms_day
                        "11n" -> R.drawable.thunderstorms_night
                        "13d" -> R.drawable.snowy_day
                        "13n" -> R.drawable.snowy_night
                        "50d" -> R.drawable.haze_day
                        "50n" -> R.drawable.haze_night

                        else -> R.drawable.question

                    }


                    binding.ivWeatherIcon.setImageResource(iconResId) // İkonu imageView'e set etme
                    binding.main.setBackground(backgroundDrawable)

                    val tempCelsius = weatherResponse?.main?.temp
                    if (tempCelsius != null) {
                        binding.tvTemperatureCelcius.text = "${tempCelsius}°C"
                        binding.tvTemperatureKelvin.text = "${tempCelsius + 273.15} K"
                        binding.tvTemperatureFahrenheit.text = "${(tempCelsius * 9 / 5) + 32}°F"
                    } else {
                        binding.tvTemperatureCelcius.text = "yok"
                        binding.tvTemperatureKelvin.text = "yok"
                        binding.tvTemperatureFahrenheit.text = "yok"
                    }

                    binding.tvWeatherDescription.text = weatherResponse?.weather?.get(0)?.description
                    binding.tvHumidity.text = "Nem: ${weatherResponse?.main?.humidity}%"
                    binding.tvWindSpeed.text = "Rüzgar Hızı: ${weatherResponse?.wind?.speed} m/s"
                    binding.tvPressure.text = "Basınç: ${weatherResponse?.main?.pressure} hPa"

                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("kontrol", "fail")
            }
        })
    }

}