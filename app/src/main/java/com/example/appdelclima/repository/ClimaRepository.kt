package com.example.appdelclima.repository

import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.model.ClimaResponse
import com.example.appdelclima.model.PronosticoResponse
import retrofit2.Response

class ClimaRepository(private val climaApi: ClimaApiService) {

    suspend fun obtenerClimaPorCiudad(ciudad: String, apiKey: String): Response<ClimaResponse> {
        return climaApi.obtenerClimaPorCiudad(ciudad, apiKey)
    }

    suspend fun obtenerClimaPorCoordenadas(lat: Double, lon: Double, apiKey: String): Response<ClimaResponse> {
        return climaApi.obtenerClimaPorCoordenadas(lat, lon, apiKey)
    }

    suspend fun obtenerPronosticoExtendido(ciudad: String, apiKey: String): Response<PronosticoResponse> {
        return climaApi.obtenerPronosticoExtendido(ciudad, apiKey)
    }
}
