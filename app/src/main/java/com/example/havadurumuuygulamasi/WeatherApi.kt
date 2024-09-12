package com.example.havadurumuuygulamasi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    //bu fonk main içinde kulanılacak
    fun getCurrentWeather(
        @Query("q") q: String, //kordinat bilgileriydi q ya dönüştürdük
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric" ,// standart Celcius alıyoda bunu // farklı birimlere de çevir
        @Query("lang") lang: String = "tr" // Türkçe dil desteği

    ):Call<WeatherResponse> //api cevabı data class türünde olacak
}
