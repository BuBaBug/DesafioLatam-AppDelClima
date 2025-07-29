package com.example.appdelclima.repository

import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.model.ClimaResponse
import com.example.appdelclima.model.PronosticoResponse
import retrofit2.Response

class ClimaRepository(private val apiService: ClimaApiService) {

    suspend fun obtenerClimaActual(ciudad: String, apiKey: String): Response<ClimaResponse> {
        return apiService.obtenerClimaPorCiudad(ciudad, apiKey)
    }

    suspend fun obtenerPronostico(ciudad: String, apiKey: String): Response<PronosticoResponse> {
        return apiService.obtenerPronosticoExtendido(ciudad, apiKey)
    }
}
