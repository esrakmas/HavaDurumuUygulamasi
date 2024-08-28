package com.example.havadurumuuygulamasi

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

/*
response içindeki hiyerarşiyi sağladığımız basit yapı

Uzak sunucudan bizlere dönecek json formatındaki verinin
çeşitli coverterlar yardımı ile cast-map edileceği sınıflar

**/



data class WeatherResponse(
 //alıcağımız değerler
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long,
)

data class Coord(
    val lon: Double,
    val lat: Double,
)

data class Weather(
    val id: Long,
    val main: String,
    val description: String, // bunu kullanabilirim dil seçeneği eklenmiş olur
    val icon: String, // buna da tekrar bak bi png kullandın ama svg dene
)

data class Main(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Long,
    val humidity: Long,
    @SerializedName("sea_level")
    val seaLevel: Long,
    @SerializedName("grnd_level")
    val grndLevel: Long,
)

data class Wind(
    val speed: Double,
    val deg: Long,
    val gust: Double,
)

data class Clouds(
    val all: Long,
)

data class Sys(
    val type: Long,
    val id: Long,
    val country: String, // ülke kodu eklenebilir
    val sunrise: Long,  //
    val sunset: Long,  // gün doğumju ve batımı eklenebilir
)
