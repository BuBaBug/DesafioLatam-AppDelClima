package com.example.appdelclima

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.adapter.PronosticoAdapter
import com.example.appdelclima.model.DiaPronostico
import com.example.appdelclima.model.PronosticoResponse
import com.example.appdelclima.repository.ClimaRepository
import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.api.RetrofitClient
import com.example.appdelclima.utils.Constants
import com.example.appdelclima.viewmodel.ClimaViewModel
import com.example.appdelclima.viewmodel.ClimaViewModelFactory

class PronosticoActivity : AppCompatActivity() {

    private lateinit var recyclerPronostico: RecyclerView
    private lateinit var adapter: PronosticoAdapter

    private val apiKey = Constants.API_KEY

    private val apiService by lazy {
        RetrofitClient.getInstance().create(ClimaApiService::class.java)
    }

    private val repository by lazy {
        ClimaRepository(apiService)
    }

    private val climaViewModel: ClimaViewModel by viewModels {
        ClimaViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronostico)

        recyclerPronostico = findViewById(R.id.recyclerPronostico)
        recyclerPronostico.layoutManager = LinearLayoutManager(this)
        adapter = PronosticoAdapter(emptyList())
        recyclerPronostico.adapter = adapter

        val ciudad = intent.getStringExtra("ciudad") ?: ""
        if (ciudad.isNotEmpty()) {
            climaViewModel.obtenerPronostico(ciudad, apiKey)
        } else {
            Toast.makeText(this, "Ciudad no válida", Toast.LENGTH_SHORT).show()
            finish() // Cierra activity si ciudad inválida
        }

        climaViewModel.pronostico.observe(this) { pronosticoResponse ->
            val listaSimplificada = procesarPronostico(pronosticoResponse)
            adapter.actualizarDatos(listaSimplificada)
        }

        climaViewModel.error.observe(this) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Transforma la lista compleja de pronóstico en una lista simple para el adapter,
     * extrayendo fecha, temperatura y descripción.
     */
    private fun procesarPronostico(pronosticoResponse: PronosticoResponse): List<DiaPronostico> {
        return pronosticoResponse.list.map { item ->
            DiaPronostico(
                fecha = item.dt_txt,
                temperatura = item.main.temp,
                descripcion = item.weather.firstOrNull()?.description ?: "Sin descripción"
            )
        }
    }
}
