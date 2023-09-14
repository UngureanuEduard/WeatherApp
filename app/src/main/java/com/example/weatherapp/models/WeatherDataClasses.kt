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
            val condition: Condition,
            val is_day: Double
        )

        data class Condition(
            val text: String,
            val icon: String
        )

        data class ForecastResponse(
            val forecast: Forecast
        )

        data class Forecast(
            val forecastday: List<ForecastDay>
        )

        data class ForecastDay(
            val date: String,
            val date_epoch: Int,
            val day: ForecastDayInfo,
        )

        data class ForecastDayInfo(
            val maxtemp_c: Double,
            val maxtemp_f: Double,
            val mintemp_c: Double,
            val mintemp_f: Double,
            val avgtemp_c: Double,
            val avgtemp_f: Double,
            val maxwind_mph: Double,
            val maxwind_kph: Double,
            val totalprecip_mm: Double,
            val totalprecip_in: Double,
            val avgvis_km: Double,
            val avgvis_miles: Double,
            val avghumidity: Int,
            val condition: Condition
        )


    }