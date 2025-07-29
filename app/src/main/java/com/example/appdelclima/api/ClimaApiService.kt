package com.example.appdelclima.api

import com.example.appdelclima.model.ClimaResponse
import com.example.appdelclima.model.PronosticoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApiService {

    @GET("weather")
    suspend fun obtenerClimaPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): Response<ClimaResponse>

    // 🔮 Nuevo: pronóstico extendido (cada 3 horas por 5 días)
    @GET("forecast")
    suspend fun obtenerPronosticoExtendido(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "es"
    ): Response<PronosticoResponse>
}
