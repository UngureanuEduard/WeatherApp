package com.example.weatherapp.models

class WeatherDataClasses {

    data class WeatherResponse(
        val location: Location,
        val current: CurrentWeather
    )

    data class Location(
        val name: String,
        val region: String,
        val country: String,
        val lat: Double,
        val lon: Double
    )

    data class CurrentWeather(
        val temp_c: Double,
        val condition: Condition
    )

    data class Condition(
        val text: String,
        val icon: String
    )

}