# Weather App

![Screenshot_20230914_111946](https://github.com/UngureanuEduard/WeatherApp/assets/130817880/ebf0b949-5195-405e-a8fd-b53bbadd6ede)

## Overview

The Weather App is a simple Android application that provides real-time weather information based on the user's current location. It uses the Weather API to fetch weather data and display it in a user-friendly interface. This README provides an overview of the app's features, how to set it up, and how to use it.

## Features

- **Real-Time Weather Data:** The app fetches real-time weather data based on the user's current location.

- **Dynamic Background:** The app dynamically changes its background based on whether it's day or night at the user's location.

- **3-Day Weather Forecast:** In addition to current weather information, the app also displays a 3-day weather forecast.

- **Location Permission Handling:** The app handles location permission requests and gracefully handles scenarios where location access is denied.

## Getting Started

Follow these steps to set up and run the Weather App on your Android device:

1. **Clone the Repository:** Clone this repository to your local machine using Git:

   ```
   git clone https://github.com/your-username/weather-app.git
   ```

2. **Open in Android Studio:** Open the project in Android Studio.

3. **API Key Configuration:** Obtain an API key from [WeatherApi](https://www.weatherapi.com/) and replace the placeholder API key in the `fetchWeatherData` function with your actual API key:

   ```kotlin
   val apiKey = "your_api_key_here"
   ```

4. **Run the App:** Connect your Android device or use an emulator, and run the app from Android Studio.

5. **Location Permission:** When you first launch the app, it will request location permission. Grant the permission for the app to access your location.

## Usage

- Upon launching the app and granting location permission, it will automatically detect your location and display the current weather conditions.

- The background will change dynamically based on whether it's day or night at your location.

- You can view the 3-day weather forecast by scrolling down on the main screen.

## Dependencies

- Retrofit: Used for making API requests to fetch weather data.

- Glide: Used for loading and displaying weather icons.


## Acknowledgments

- This app uses the Weather API to fetch weather data. Thanks to Weather API for providing weather information.

---

Happy Weather Checking! 🌦️🌧️🌤️
