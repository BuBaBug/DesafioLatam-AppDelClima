package com.example.appdelclima.viewmodel

import androidx.lifecycle.*
import com.example.appdelclima.model.ClimaResponse
import com.example.appdelclima.model.PronosticoResponse
import com.example.appdelclima.repository.ClimaRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class ClimaViewModel(private val repository: ClimaRepository) : ViewModel() {

    // LiveData para clima actual
    private val _climaActual = MutableLiveData<ClimaResponse>()
    val climaActual: LiveData<ClimaResponse> get() = _climaActual

    // LiveData para pronóstico extendido
    private val _pronostico = MutableLiveData<PronosticoResponse>()
    val pronostico: LiveData<PronosticoResponse> get() = _pronostico

    // LiveData para errores
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Función para obtener clima actual por ciudad
    fun obtenerClima(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response: Response<ClimaResponse> = repository.obtenerClimaPorCiudad(ciudad, apiKey)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _climaActual.value = it
                    } ?: run {
                        _error.value = "Respuesta vacía del servidor (clima actual)"
                    }
                } else {
                    _error.value = "Error clima actual: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red (clima actual): ${e.message}"
            }
        }
    }

    // Función para obtener pronóstico extendido por ciudad
    fun obtenerPronostico(ciudad: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val response: Response<PronosticoResponse> = repository.obtenerPronosticoExtendido(ciudad, apiKey)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _pronostico.value = it
                    } ?: run {
                        _error.value = "Respuesta vacía del servidor (pronóstico)"
                    }
                } else {
                    _error.value = "Error pronóstico: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red (pronóstico): ${e.message}"
            }
        }
    }

    fun obtenerClimaPorCoordenadas(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = repository.obtenerClimaPorCoordenadas(lat, lon, apiKey)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _climaActual.value = it
                    } ?: run {
                        _error.value = "Respuesta vacía del servidor (clima por coordenadas)"
                    }
                } else {
                    _error.value = "Error clima por coordenadas: ${response.code()} ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de red (clima por coordenadas): ${e.message}"
            }
        }
    }

}
