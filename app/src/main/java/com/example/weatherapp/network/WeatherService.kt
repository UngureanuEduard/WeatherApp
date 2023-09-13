package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherDataClasses
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("/v1/current.json") // Use the appropriate API endpoint
    fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Call<WeatherDataClasses.WeatherResponse> // WeatherResponse is a data class to hold the API response
}
