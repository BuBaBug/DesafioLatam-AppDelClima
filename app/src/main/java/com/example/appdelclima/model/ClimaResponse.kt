package com.example.appdelclima.model

data class ClimaResponse(
    val name: String,
    val main: Main,
    val wind: Wind,
    val weather: List<Weather>
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)

data class Weather(
    val description: String,
    val icon: String
)