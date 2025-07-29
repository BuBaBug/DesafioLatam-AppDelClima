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

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Obtiene la última ubicación conocida (puede ser null)
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Location? {
        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    // Convierte latitud y longitud en nombre de ciudad
    fun obtenerCiudadDesdeCoordenadas(lat: Double, lon: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("es"))
            val lista = geocoder.getFromLocation(lat, lon, 1)
            if (lista != null && lista.isNotEmpty()) {
                lista[0].locality ?: lista[0].adminArea ?: lista[0].countryName
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
