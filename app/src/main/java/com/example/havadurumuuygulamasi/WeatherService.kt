package com.example.havadurumuuygulamasi

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/*
Retrofit nesnesini oluşturup,
 API isteklerini gerçekleştirmek için bir servis nesnesi oluşturacağız.
 Bu nesne, API tabanlı işlemleri gerçekleştirmek için kullanılacak.

*/
object WeatherService {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val weatherApi: WeatherApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)
}
