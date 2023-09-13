package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherDataClasses
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("/v1/current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Call<WeatherDataClasses.WeatherResponse>

    @GET("/v1/forecast.json")
    fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int
    ): Call<WeatherDataClasses.ForecastResponse>

    @GET("/v1/forecast.json")
    fun getCurrentTime(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("days") days: Int
    ): Call<WeatherDataClasses.ForecastResponse>
}


