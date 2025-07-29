package com.example.appdelclima

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.api.RetrofitClient
import com.example.appdelclima.repository.ClimaRepository
import com.example.appdelclima.utils.Constants
import com.example.appdelclima.viewmodel.ClimaViewModel
import com.example.appdelclima.viewmodel.ClimaViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var etCiudad: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnUbicacion: Button
    private lateinit var tvTemperatura: TextView
    private lateinit var tvCiudad: TextView

    private val apiKey = Constants.API_KEY

    private val apiService: ClimaApiService by lazy {
        RetrofitClient.getInstance().create(ClimaApiService::class.java)
    }
    private val repository: ClimaRepository by lazy {
        ClimaRepository(apiService)
    }

    private val climaViewModel: ClimaViewModel by viewModels {
        ClimaViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCiudad = findViewById(R.id.etCiudad)
        btnBuscar = findViewById(R.id.btnBuscar)
        btnUbicacion = findViewById(R.id.btnUbicacion)
        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvCiudad = findViewById(R.id.tvCiudad)

        btnBuscar.setOnClickListener {
            val ciudad = etCiudad.text.toString().trim()
            if (ciudad.isNotEmpty()) {
                obtenerClima(ciudad)
                navegarAPronostico(ciudad)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        climaViewModel.climaActual.observe(this) { clima ->
            tvTemperatura.text = "🌡️ ${clima.main.temp.toInt()}°C"
            tvCiudad.text = "🏙️ ${clima.name}"
        }

        climaViewModel.error.observe(this) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        // Si quieres usar la ubicación, deberías configurar btnUbicacion aquí.
    }

    private fun obtenerClima(ciudad: String) {
        climaViewModel.obtenerClima(ciudad, apiKey)
    }

    private fun navegarAPronostico(ciudad: String) {
        val intent = Intent(this, PronosticoActivity::class.java)
        intent.putExtra("ciudad", ciudad)
        startActivity(intent)
    }
}
