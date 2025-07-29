package com.example.appdelclima.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.appdelclima.model.ClimaResponse
import com.example.appdelclima.model.PronosticoResponse
import com.example.appdelclima.repository.ClimaRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ClimaViewModel(private val repository: ClimaRepository) : ViewModel() {

    private val _climaActual = MutableLiveData<ClimaResponse>()
    val climaActual: LiveData<ClimaResponse> = _climaActual

    private val _pronostico = MutableLiveData<PronosticoResponse>()
    val pronostico: LiveData<PronosticoResponse> = _pronostico

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun obtenerClima(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response: Response<ClimaResponse> = repository.obtenerClimaActual(ciudad, apiKey)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _climaActual.value = it
                    } ?: run {
                        _error.value = "Respuesta vacía del servidor"
                    }
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            }
        }
    }

    fun obtenerPronostico(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response: Response<PronosticoResponse> = repository.obtenerPronostico(ciudad, apiKey)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _pronostico.value = it
                    } ?: run {
                        _error.value = "Respuesta vacía del servidor"
                    }
                } else {
                    _error.value = "Error: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red: ${e.message}"
            }
        }
    }
}