package com.example.appdelclima.utils

import android.content.Context
import com.example.appdelclima.model.ClimaResponse
import com.example.appdelclima.model.PronosticoResponse
import com.google.gson.Gson

object CacheManager {
    private const val PREFS_NAME = "cache_prefs"
    private const val KEY_CLIMA = "cached_clima"
    private const val KEY_PRONOSTICO = "cached_pronostico"
    private val gson = Gson()

    fun guardarClima(context: Context, clima: ClimaResponse) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CLIMA, gson.toJson(clima)).apply()
    }

    fun guardarPronostico(context: Context, pronostico: PronosticoResponse) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PRONOSTICO, gson.toJson(pronostico)).apply()
    }

    fun obtenerClima(context: Context): ClimaResponse? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CLIMA, null)
        return json?.let { gson.fromJson(it, ClimaResponse::class.java) }
    }

    fun obtenerPronostico(context: Context): PronosticoResponse? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_PRONOSTICO, null)
        return json?.let { gson.fromJson(it, PronosticoResponse::class.java) }
    }
}
