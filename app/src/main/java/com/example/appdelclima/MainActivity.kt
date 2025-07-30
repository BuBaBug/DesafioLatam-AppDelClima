package com.example.appdelclima

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.adapter.FavoritosAdapter
import com.example.appdelclima.adapter.PronosticoAdapter
import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.api.RetrofitClient
import com.example.appdelclima.repository.ClimaRepository
import com.example.appdelclima.utils.CacheManager
import com.example.appdelclima.utils.Constants
import com.example.appdelclima.viewmodel.ClimaViewModel
import com.example.appdelclima.viewmodel.ClimaViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var etCiudad: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnUbicacion: Button



    private lateinit var tvTemperatura: TextView
    private lateinit var tvCiudad: TextView

    private lateinit var recyclerFavoritos: RecyclerView
    private lateinit var favoritosAdapter: FavoritosAdapter

    private lateinit var recyclerPronostico: RecyclerView
    private lateinit var pronosticoAdapter: PronosticoAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        etCiudad = findViewById(R.id.etCiudad)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnUbicacion = findViewById(R.id.btnUbicacion)
        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvCiudad = findViewById(R.id.tvCiudad)
        recyclerFavoritos = findViewById(R.id.recyclerFavoritos)
        recyclerPronostico = findViewById(R.id.recyclerPronostico)

        // Setup RecyclerView para favoritos
        favoritosAdapter = FavoritosAdapter(emptyList()) { ciudadSeleccionada ->
            etCiudad.setText(ciudadSeleccionada)
            buscarClimaYPronostico(ciudadSeleccionada)
        }
        recyclerFavoritos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerFavoritos.adapter = favoritosAdapter
        actualizarListaFavoritos()

        // Setup RecyclerView para pronóstico
        pronosticoAdapter = PronosticoAdapter(emptyList())
        recyclerPronostico.layoutManager = LinearLayoutManager(this)
        recyclerPronostico.adapter = pronosticoAdapter

        recyclerFavoritos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

// Obtener favoritos guardados
        val favoritos = FavoritosManager.obtenerFavoritos(this)

        // Crear adapter con favoritos guardados
        favoritosAdapter = FavoritosAdapter(favoritos) { ciudadSeleccionada ->
            etCiudad.setText(ciudadSeleccionada)
            buscarClimaYPronostico(ciudadSeleccionada)
        }

        recyclerFavoritos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        favoritosAdapter = FavoritosAdapter(emptyList()) { ciudad ->
            etCiudad.setText(ciudad)
            buscarClimaYPronostico(ciudad)
        }
        recyclerFavoritos.adapter = favoritosAdapter
        actualizarListaFavoritos()




        // Setup RecyclerView para pronóstico
        pronosticoAdapter = PronosticoAdapter(emptyList())
        recyclerPronostico.layoutManager = LinearLayoutManager(this)
        recyclerPronostico.adapter = pronosticoAdapter


        // Botón buscar
        btnBuscar.setOnClickListener {
            val ciudad = etCiudad.text.toString().trim()
            if (ciudad.isNotEmpty()) {
                FavoritosManager.agregarFavorito(this, ciudad)
                actualizarListaFavoritos()
                buscarClimaYPronostico(ciudad)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón ubicación - Aquí asumo que tienes LocationHelper o similar para obtener ubicación
        btnUbicacion.setOnClickListener {
            // Aquí implementarías lógica para obtener ubicación y llamar climaViewModel
            Toast.makeText(this, "Funcionalidad de ubicación aún no implementada", Toast.LENGTH_SHORT).show()
        }

        // Observadores LiveData para clima actual
        climaViewModel.climaActual.observe(this) { clima ->
            if (clima != null) {
                CacheManager.guardarClima(this, clima)
                tvTemperatura.text = "🌡️ ${clima.main.temp.toInt()}°C"
                tvCiudad.text = "🏙️ ${clima.name}"
            }
        }

        // Observador LiveData para pronóstico
        climaViewModel.pronostico.observe(this) { pronosticoResponse ->
            if (pronosticoResponse != null) {
                pronosticoAdapter = PronosticoAdapter(pronosticoResponse.list)
                recyclerPronostico.adapter = pronosticoAdapter
            }
        }

        // Observador LiveData para errores
        climaViewModel.error.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        val ciudadConsultada = etCiudad.text.toString().trim()
        if (ciudadConsultada.isNotEmpty()) {
            FavoritosManager.agregarFavorito(this, ciudadConsultada)
            actualizarListaFavoritos()
        }


    }

    private fun buscarClimaYPronostico(ciudad: String) {
        climaViewModel.obtenerClima(ciudad, apiKey)
        climaViewModel.obtenerPronostico(ciudad, apiKey)
    }

    private fun actualizarListaFavoritos() {
        val favoritos = FavoritosManager.obtenerFavoritos(this)
        favoritosAdapter.actualizarLista(favoritos)
    }

}
