package com.example.havadurumuuygulamasi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
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
    private lateinit var suggestionsAdapter: ArrayAdapter<String>

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

        //yapıyı olustumak bir de veriyi kaydetmek icin editor
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val lastSearchedCity = sharedPreferences.getString("lastSearchedCity", null)

        // Öneri listesini oluştur
        //ArrayAdapter(Context context, int resource, List<T> objects)
        suggestionsAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            getRecentCities()
        )
        //listview e adapteri bağla
        binding.suggestionsListView.adapter = suggestionsAdapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val cityName = query ?: return false

                //ekranı kaydetmek icin
                editor.putString("lastSearchedCity", cityName)
                editor.apply()

                fetchWeatherData(apiKey, cityName)
                updateRecentCitiesList(cityName)
                showRecentCitiesInConsole()

                binding.suggestionsListView.visibility = View.GONE

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //listview gelince işte alttan liste çıksın falan

                if (newText.isNullOrEmpty()) {
                    //öneri yoksa da görünmesin
                    binding.suggestionsListView.visibility = View.GONE
                } else {
                    updateSuggestions(newText)
                }
                return true
            }
        })

        //öneriye bastığımda searhviewde yerini alsın ve liste kapansın
        binding.suggestionsListView.setOnItemClickListener { parent, view, position, id ->
            //parent.getItemAtPosition(position):tıklanan ögenin verisini al stringe dönüşütr
            val selectedCity = parent.getItemAtPosition(position) as String
            //searchview sorgusunu selectedCity olarak ayarlıyor ve true ile arma işlemini başlat
            binding.searchView.setQuery(selectedCity, true)
            binding.suggestionsListView.visibility = View.GONE
        }

        //eğer sehir aratılmıssa önceden son arama mevcutsa
        if (lastSearchedCity != null) {
            binding.searchView.setQuery(lastSearchedCity, false)


            // son aramaya göre vevrileri geitr
            fetchWeatherData(apiKey, lastSearchedCity)

        }

        //kalvye kapansın diyr
        binding.main.setOnTouchListener { view, event ->
            // arama kutusunu temizle
            binding.searchView.setQuery("", false)
            binding.searchView.clearFocus()
            // kalvyeyi kapa
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.main.windowToken, 0)

            false
        }
    }

    private fun updateSuggestions(newText: String) {
        val filteredCities = getRecentCities().filter {
            it.contains(newText, ignoreCase = true)
        }

        if (filteredCities.isNotEmpty()) {
            suggestionsAdapter.clear()
            suggestionsAdapter.addAll(filteredCities)
            suggestionsAdapter.notifyDataSetChanged()
            binding.suggestionsListView.visibility = View.VISIBLE
        } else {
            binding.suggestionsListView.visibility = View.GONE
        }
    }

    private fun updateRecentCitiesList(cityName: String) {
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        //  son 3 liste
        val recentCitiesString = sharedPreferences.getString("recentCities", null)

        if (!recentCitiesString.isNullOrEmpty()) {
            // stringi virgüle göre ayır ve liste döner virgül zorunlu dğeil
            val recentCities: ArrayList<String> =
                recentCitiesString.split(",").map { it.trim() } as ArrayList

            if (!recentCities.contains(cityName)) {
                if (recentCities.size > 2) {
                    recentCities.removeAt(2)
                }
                recentCities.add(0, cityName)
                // kaydet
                editor.putString("recentCities", recentCities.joinToString(","))
                editor.apply()
            }
        } else {
            // kaydet
            editor.putString("recentCities", cityName)
            editor.apply()
        }
    }

    private fun getRecentCities(): List<String> {
        val sharedPreferences = getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
        val recentCitiesString = sharedPreferences.getString("recentCities", null)


        return if (!recentCitiesString.isNullOrEmpty()) {
            recentCitiesString.split(",").map { it.trim() }

        } else {
            listOf()
        }

    }

    private fun showRecentCitiesInConsole() {
        val recentCities = getRecentCities()

        // Konsolda göstermek için
        Log.d("RecentCities", "Son uc sehir : ${recentCities.joinToString(", ")}")
    }

    //apiden veri çekme fonksiyonu
    private fun fetchWeatherData(apiKey: String, cityName: String) {
        WeatherService.weatherApi.getCurrentWeather(cityName, apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                //enqueue Metodu: Retrofit kütüphanesinde, enqueue metodu asenkron bir ağ çağrısı yapar.
                // Bu çağrı, çalıştırıldığında sunucuya bir istek gönderir
                // ve yanıt alındığında (bu başarı veya başarısızlık olabilir)
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
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
                            "01n" -> R.drawable.moon
                            "02d" -> R.drawable.cloudy_day
                            "02n" -> R.drawable.cloudy_night
                            "03d", "03n" -> R.drawable.cloudy
                            "04d", "04n" -> R.drawable.cloudy
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

                            binding.tvTemperatureCelcius.text = String.format("%.1f°C", tempCelsius)
                            binding.tvTemperatureKelvin.text =
                                String.format("%.2f K", tempCelsius + 273.15)
                            binding.tvTemperatureFahrenheit.text =
                                String.format("%.2f°F", (tempCelsius * 9 / 5) + 32)

                        } else {
                            binding.tvTemperatureCelcius.text = "yok"
                            binding.tvTemperatureKelvin.text = "yok"
                            binding.tvTemperatureFahrenheit.text = "yok"
                        }

                        binding.tvWeatherDescription.text =
                            weatherResponse?.weather?.get(0)?.description
                        binding.tvHumidity.text = "Nem: ${weatherResponse?.main?.humidity}%"
                        binding.tvWindSpeed.text =
                            "Rüzgar Hızı: ${weatherResponse?.wind?.speed} m/s"
                        binding.tvPressure.text = "Basınç: ${weatherResponse?.main?.pressure} hPa"

                        //veriler gelince layout görünürlüğü aç tv kaldır
                        binding.weatherData.visibility = View.VISIBLE
                        binding.tvPromptMessage.visibility = View.GONE

                    }

                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.d("kontrol", "fail")
                }
            })
    }
}