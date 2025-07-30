package com.example.appdelclima.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationHelper(private val context: Context) {

    // Cliente de ubicación de Google Play Services
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Obtiene la última ubicación conocida del dispositivo.
     * Puede devolver null si no hay ubicación disponible o si ocurre un error.
     * El uso de 'await()' permite suspender la coroutine hasta que la ubicación esté lista.
     * Nota: Se debe manejar el permiso ACCESS_FINE_LOCATION fuera de esta función.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            // Log o manejo del error pueden ser añadidos aquí si quieres
            null
        }
    }

    /**
     * Convierte coordenadas geográficas a nombre de ciudad usando Geocoder.
     * Retorna nombre de localidad, área administrativa o país, según disponibilidad.
     * Retorna null si no se encuentra ninguna ubicación o si ocurre un error.
     */
    fun obtenerCiudadDesdeCoordenadas(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("es")) // Localización en español
            val lista = geocoder.getFromLocation(lat, lon, 1)
            if (!lista.isNullOrEmpty()) {
                lista[0].locality ?: lista[0].adminArea ?: lista[0].countryName
            } else {
                null
            }
        } catch (e: Exception) {
            // Aquí también podrías loguear el error
            null
        }
    }
}
