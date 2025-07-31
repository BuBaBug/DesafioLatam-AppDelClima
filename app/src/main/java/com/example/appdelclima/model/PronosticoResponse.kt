package com.example.appdelclima.model

data class PronosticoResponse(
    val list: List<PronosticoItem>,
    val city: Ciudad
)

data class PronosticoItem(
    val dt_txt: String,
    val main: MainPronostico,
    val weather: List<Weather>
)
