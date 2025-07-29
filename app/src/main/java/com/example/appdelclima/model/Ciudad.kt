package com.example.appdelclima.model

data class Ciudad(
    val id: Int,
    val name: String,
    val country: String,
    val coord: Coordenadas
)

data class Coordenadas(
    val lat: Double,
    val lon: Double
)