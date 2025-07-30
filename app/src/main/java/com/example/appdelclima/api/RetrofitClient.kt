package com.example.appdelclima.api

import com.example.appdelclima.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Lazy inicialización: se crea solo cuando se usa por primera vez
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // URL base para todas las llamadas
            .addConverterFactory(GsonConverterFactory.create()) // Convierte JSON a objetos Kotlin
            .build()
    }

    // Función para obtener la instancia Retrofit
    fun getInstance(): Retrofit = retrofit
}
