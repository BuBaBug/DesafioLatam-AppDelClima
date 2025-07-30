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
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): Response<ClimaResponse>

    @GET("forecast")
    suspend fun obtenerPronosticoExtendido(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): Response<PronosticoResponse>

    @GET("weather")
    suspend fun obtenerClimaPorCoordenadas(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("appid") apiKey: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): Response<ClimaResponse>

}
