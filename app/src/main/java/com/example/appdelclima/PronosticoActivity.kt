package com.example.appdelclima

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.adapter.PronosticoAdapter
import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.api.RetrofitClient
import com.example.appdelclima.model.DiaPronostico
import com.example.appdelclima.model.PronosticoResponse
import com.example.appdelclima.repository.ClimaRepository
import com.example.appdelclima.utils.Constants
import com.example.appdelclima.viewmodel.ClimaViewModel
import com.example.appdelclima.viewmodel.ClimaViewModelFactory

class PronosticoActivity : AppCompatActivity() {

    private lateinit var recyclerPronostico: RecyclerView
    private lateinit var adapter: PronosticoAdapter

    private val apiKey = Constants.API_KEY

    private val apiService: ClimaApiService by lazy {
        RetrofitClient.getInstance().create(ClimaApiService::class.java)
    }

    private val climaRepository: ClimaRepository by lazy {
        ClimaRepository(apiService)
    }

    private val climaViewModel: ClimaViewModel by viewModels {
        ClimaViewModelFactory(climaRepository)
    }

    companion object {
        const val EXTRA_CIUDAD = "ciudad"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronostico)

        recyclerPronostico = findViewById(R.id.recyclerPronostico)
        recyclerPronostico.layoutManager = LinearLayoutManager(this)
        adapter = PronosticoAdapter(emptyList())
        recyclerPronostico.adapter = adapter

        val ciudad = intent.getStringExtra(EXTRA_CIUDAD) ?: ""
        if (ciudad.isNotEmpty()) {
            climaViewModel.obtenerPronostico(ciudad, apiKey)
        } else {
            Toast.makeText(this, "Ciudad no válida", Toast.LENGTH_SHORT).show()
            finish()
        }



        climaViewModel.pronostico.observe(this) { pronosticoResponse ->
            pronosticoResponse?.let {
                val listaProcesada = procesarPronostico(it)
                adapter.actualizarDatos(listaProcesada)
            }
        }


        climaViewModel.error.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun procesarPronostico(pronosticoResponse: PronosticoResponse): List<DiaPronostico> {
        val agrupado = pronosticoResponse.list.groupBy { it.dt_txt.substring(0, 10) }

        return agrupado.entries.take(5).map { (fecha, itemsDelDia) ->
            val tempMin = itemsDelDia.minOf { it.main.temp_min }
            val tempMax = itemsDelDia.maxOf { it.main.temp_max }
            val temperaturaPromedio = (tempMin + tempMax) / 2

            val descripcion = itemsDelDia[0].weather[0].description

            DiaPronostico(
                fecha = fecha,
                temperatura = temperaturaPromedio,
                descripcion = descripcion
            )
        }
    }


}

