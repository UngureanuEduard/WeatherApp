package com.example.weatherapp
import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.Context
import android.location.Location
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.weatherapp.models.WeatherDataClasses
import com.example.weatherapp.network.WeatherService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the location provider with the application context
        locationProvider = LocationProvider(applicationContext)

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, proceed with location detection
            // Call a function to start location detection
            startLocationDetection()
        } else {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION  // Use the defined request code
            )
        }
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)  // Call superclass method

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start location detection
                    startLocationDetection()
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)
                    showToast("Location permission denied.")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Automatically retrieve and display location when the activity resumes
        getLocation()
    }

    // Function to start location detection
    private fun startLocationDetection() {
        // Get the last known location
        locationProvider.getLastLocation { location ->
            if (location != null) {
                // Fetch weather data using the location's latitude and longitude
                val latitude = location.latitude
                val longitude = location.longitude
                fetchWeatherData(latitude, longitude)
            } else {
                // Handle location retrieval failure
                showToast("Unable to retrieve location.")
            }
        }
    }


    // Function to get location and display it
    private fun getLocation() {
        // Get the last known location
        locationProvider.getLastLocation { location ->
            if (location == null) {
                // Handle location retrieval failure
                // Example: Show an error message to the user
                showToast("Unable to retrieve location.")
            }
            // No need to update txtLocation with latitude and longitude
        }
    }

    // Function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    class LocationProvider(private val context: Context) {
        private val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // Function to get the last known location
        fun getLastLocation(callback: (Location?) -> Unit) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    callback(location)
                }
                .addOnFailureListener {
                    // Handle location retrieval failure here
                    callback(null)
                }
        }
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val apiKey = "69483e4cc8354ae8a47140957231209"
        val query = "$latitude,$longitude"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val call = service.getCurrentWeather(apiKey, query)

        call.enqueue(object : Callback<WeatherDataClasses.WeatherResponse> {
            override fun onResponse(call: Call<WeatherDataClasses.WeatherResponse>, response: Response<WeatherDataClasses.WeatherResponse>) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        val currentWeather = weatherResponse.current
                        val temperature = currentWeather.temp_c
                        val condition = currentWeather.condition.text
                        val location = weatherResponse.location
                        val country = location.country
                        val county = location.region
                        val iconUrl = "https:" + currentWeather.condition.icon  // Build the full URL for the icon

                        val temperatureTextView = findViewById<TextView>(R.id.txtTemperature)
                        val conditionTextView = findViewById<TextView>(R.id.txtCondition)
                        val countryTextView = findViewById<TextView>(R.id.txtCountry)
                        val weatherIconImageView = findViewById<ImageView>(R.id.image)

                        // Update the TextViews
                        temperatureTextView.text = "$temperature°C"
                        conditionTextView.text = condition
                        countryTextView.text = "$country, $county"

                        // Load the weather icon using Glide and set it to the existing ImageView
                        Glide.with(this@MainActivity)
                            .load(iconUrl)
                            .into(weatherIconImageView)
                    }
                } else {
                    // Handle API error
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherDataClasses.WeatherResponse>, t: Throwable) {
                // Handle network error
                showToast("Network error: ${t.message}")
            }
        })

        // Fetch 3-day forecast data
        val forecastCall = service.getWeatherForecast(apiKey, query, 4) // Request a 4-day forecast
        forecastCall.enqueue(object : Callback<WeatherDataClasses.ForecastResponse> {
            override fun onResponse(call: Call<WeatherDataClasses.ForecastResponse>, response: Response<WeatherDataClasses.ForecastResponse>) {
                if (response.isSuccessful) {
                    val forecastResponse = response.body()
                    if (forecastResponse != null && forecastResponse.forecast.forecastday.isNotEmpty()) {
                        // Get the forecast data for the next 3 days
                        val forecastData = forecastResponse.forecast.forecastday.take(4)

                        // Update the UI with the 3-day forecast data
                        updateForecastUI(forecastData)
                    }
                } else {
                    // Handle forecast API error
                    showToast("Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherDataClasses.ForecastResponse>, t: Throwable) {
                // Handle network error
                showToast("Forecast API Network error: ${t.message}")
            }
        })
    }
    private fun updateForecastUI(forecastData: List<WeatherDataClasses.ForecastDay>) {
        // Assuming you have UI elements to display the forecast for each day,
        // you can update them here based on the data in forecastData.

        // For example, if you have TextViews for each day's date, temperature, and condition:
        val day1DateTextView = findViewById<TextView>(R.id.Section1Day)
        val day1TempTextView = findViewById<TextView>(R.id.Section1Temp)
        val day1ConditionImageView = findViewById<ImageView>(R.id.Section1Img)

        val day2DateTextView = findViewById<TextView>(R.id.Section2Day)
        val day2TempTextView = findViewById<TextView>(R.id.Section2Temp)
        val day2ConditionImageView = findViewById<ImageView>(R.id.Section2Img)

        val day3DateTextView = findViewById<TextView>(R.id.Section3Day)
        val day3TempTextView = findViewById<TextView>(R.id.Section3Temp)
        val day3ConditionImageView = findViewById<ImageView>(R.id.Section3Img)

        // Assuming forecastData is a list of ForecastDay objects, you can access data like this:
        if (forecastData.size >= 3) {
            // Day 1 forecast
            val day1Date = forecastData[1].date
            val day1Month = day1Date.substring(5, 7)  // Extract the month (assuming date is in "yyyy-MM-dd" format)
            val day1Day = day1Date.substring(8, 10)  // Extract the day
            day1DateTextView.text = "$day1Month/$day1Day"
            day1TempTextView.text = "${forecastData[1].day.avgtemp_c}°C"
            // Set the condition image using Glide or another image loading library
            Glide.with(this).load("https:" + forecastData[1].day.condition.icon).into(day1ConditionImageView)

            // Day 2 forecast
            val day2Date = forecastData[2].date
            val day2Month = day2Date.substring(5, 7)
            val day2Day = day2Date.substring(8, 10)
            day2DateTextView.text = "$day2Month/$day2Day"
            day2TempTextView.text = "${forecastData[2].day.avgtemp_c}°C"
            // Set the condition image for day 2
            Glide.with(this).load("https:" + forecastData[2].day.condition.icon).into(day2ConditionImageView)

            // Day 3 forecast
            val day3Date = forecastData[3].date
            val day3Month = day3Date.substring(5, 7)
            val day3Day = day3Date.substring(8, 10)
            day3DateTextView.text = "$day3Month/$day3Day"
            day3TempTextView.text = "${forecastData[3].day.avgtemp_c}°C"
            // Set the condition image for day 3
            Glide.with(this).load("https:" + forecastData[3].day.condition.icon).into(day3ConditionImageView)
        }
    }

}
