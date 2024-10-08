package com.example.havadurumuuygulamasi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    //interface kullanarak api istekelrini yapar
    val weatherApi: WeatherApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)
}
//bu nesneyi mainactv içinde getCurrentWeather fonks erişmek için kullanacağız
