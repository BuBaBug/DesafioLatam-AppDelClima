package com.example.appdelclima

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.appdelclima.adapter.PronosticoAdapter
import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.api.RetrofitClient
import com.example.appdelclima.model.DiaPronostico
import com.example.appdelclima.model.PronosticoResponse
import com.example.appdelclima.repository.ClimaRepository
import com.example.appdelclima.utils.Constants
import com.example.appdelclima.viewmodel.ClimaViewModel
import com.example.appdelclima.viewmodel.ClimaViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    private lateinit var etCiudad: EditText
    private lateinit var btnBuscar: Button
    private lateinit var btnUbicacion: Button
    private lateinit var tvTemperatura: TextView
    private lateinit var tvCiudad: TextView
    private lateinit var rvPronostico: RecyclerView
    private lateinit var pronosticoAdapter: PronosticoAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val apiKey = Constants.API_KEY
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

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
        rvPronostico = findViewById(R.id.recyclerPronostico)

        pronosticoAdapter = PronosticoAdapter(emptyList())
        rvPronostico.layoutManager = LinearLayoutManager(this)
        rvPronostico.adapter = pronosticoAdapter

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnBuscar.setOnClickListener {
            val ciudad = etCiudad.text.toString().trim()
            if (ciudad.isNotEmpty()) {
                obtenerClimaYPronostico(ciudad)
            } else {
                Toast.makeText(this, "Ingresa una ciudad", Toast.LENGTH_SHORT).show()
            }
        }

        btnUbicacion.setOnClickListener {
            verificarPermisoUbicacion()
        }

        climaViewModel.climaActual.observe(this) { clima ->
            tvTemperatura.text = "🌡️ ${clima.main.temp.toInt()}°C"
            tvCiudad.text = "🏙️ ${clima.name}"
        }

        climaViewModel.pronostico.observe(this) { pronostico ->
            val listaDias: List<DiaPronostico> = transformarPronostico(pronostico)
            pronosticoAdapter.actualizarDatos(listaDias)
        }

        climaViewModel.error.observe(this) { errorMsg ->
            if (errorMsg.isNotEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerClimaYPronostico(ciudad: String) {
        climaViewModel.obtenerClima(ciudad, apiKey)
        climaViewModel.obtenerPronostico(ciudad, apiKey)
    }

    private fun transformarPronostico(pronosticoResponse: PronosticoResponse): List<DiaPronostico> {
        val listaDiasMapeados = mutableMapOf<String, DiaPronostico>()
        for (item in pronosticoResponse.list) {
            val fecha = item.dt_txt.substring(0, 10)
            if (!listaDiasMapeados.containsKey(fecha)) {
                val temperatura = item.main.temp
                val descripcion = item.weather.firstOrNull()?.description ?: "Sin descripción"
                listaDiasMapeados[fecha] = DiaPronostico(fecha, temperatura, descripcion)
            }
        }
        return listaDiasMapeados.values.toList()
    }

    private fun verificarPermisoUbicacion() {
        val permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permiso == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacionActual()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun obtenerUbicacionActual() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                obtenerClimaPorCoordenadas(lat, lon)
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerClimaPorCoordenadas(lat: Double, lon: Double) {
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=metric&lang=es&appid=$apiKey"

        val request = JsonObjectRequest(
            com.android.volley.Request.Method.GET, url, null,
            { response ->
                val gson = Gson()
                val pronosticoResponse = gson.fromJson(response.toString(), PronosticoResponse::class.java)
                mostrarPronostico(pronosticoResponse)
            },
            {
                Toast.makeText(this, "Error al obtener el clima por ubicación", Toast.LENGTH_SHORT).show()
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun mostrarPronostico(pronosticoResponse: PronosticoResponse) {
        val listaDias = transformarPronostico(pronosticoResponse)
        pronosticoAdapter.actualizarDatos(listaDias)

        // Mostrar también nombre de ciudad (opcional)
        tvCiudad.text = "🏙️ ${pronosticoResponse.city.name}"
        tvTemperatura.text = "🌡️ ${listaDias.firstOrNull()?.temperatura?.toInt() ?: "--"}°C"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                obtenerUbicacionActual()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
