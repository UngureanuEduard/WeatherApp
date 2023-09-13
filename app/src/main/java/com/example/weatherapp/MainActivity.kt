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
import android.widget.Toast
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
    private val MY_PERMISSIONS_REQUEST_LOCATION = 1  // Define the request code
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

                        val temperatureTextView = findViewById<TextView>(R.id.txtTemperature)
                        val conditionTextView = findViewById<TextView>(R.id.txtCondition)
                        val countryTextView = findViewById<TextView>(R.id.txtCountry)

                        temperatureTextView.text = "$temperature°C"
                        conditionTextView.text = "$condition"
                        // Concatenate country and county with a comma
                        countryTextView.text = "$country, $county"
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
    }

}
