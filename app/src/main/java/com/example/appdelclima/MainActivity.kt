package com.example.appdelclima

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.adapter.FavoritosAdapter
import com.example.appdelclima.adapter.PronosticoAdapter
import com.example.appdelclima.api.ClimaApiService
import com.example.appdelclima.api.RetrofitClient
import com.example.appdelclima.model.DiaPronostico
import com.example.appdelclima.model.PronosticoItem
import com.example.appdelclima.repository.ClimaRepository
import com.example.appdelclima.utils.CacheManager
import com.example.appdelclima.utils.Constants
import com.example.appdelclima.viewmodel.ClimaViewModel
import com.example.appdelclima.viewmodel.ClimaViewModelFactory
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.Locale



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

        // Setup RecyclerView para pronóstico
        pronosticoAdapter = PronosticoAdapter(emptyList())
        recyclerPronostico.layoutManager = LinearLayoutManager(this)
        recyclerPronostico.adapter = pronosticoAdapter

        // Cargar y mostrar favoritos guardados al iniciar
        actualizarListaFavoritos()

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

        // Botón ubicación (placeholder)
        btnUbicacion.setOnClickListener {
            obtenerUbicacionActual()
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
                val pronosticoPorDia = agruparPorDia(pronosticoResponse.list)
                pronosticoAdapter = PronosticoAdapter(pronosticoPorDia)
                recyclerPronostico.adapter = pronosticoAdapter
            }
        }



        // Observador LiveData para errores
        climaViewModel.error.observe(this) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El permiso fue concedido, ahora llamamos otra vez para obtener la ubicación
                obtenerUbicacionActual()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
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

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            interval = 10000 // 10 segundos
            fastestInterval = 5000
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            numUpdates = 1  // Solo una actualización
        }

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    try {
                        val direcciones = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!direcciones.isNullOrEmpty()) {
                            val ciudad = direcciones[0].locality ?: direcciones[0].subAdminArea
                            ciudad?.let {
                                etCiudad.setText(it)
                                buscarClimaYPronostico(it)
                                FavoritosManager.agregarFavorito(this@MainActivity, it)
                                actualizarListaFavoritos()
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "No se pudo obtener la ciudad", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(this@MainActivity, "Error al obtener la ciudad", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
                }
                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }


    private fun agruparPorDia(lista: List<PronosticoItem>): List<DiaPronostico> {
        val agrupado = lista.groupBy { it.dt_txt.substring(0, 10) } // Agrupar por fecha (YYYY-MM-DD)

        return agrupado.entries.take(5).map { (fecha, itemsDelDia) ->
            val temperaturaPromedio = itemsDelDia.map { it.main.temp }.average()
            val descripcion = itemsDelDia[0].weather[0].description

            DiaPronostico(
                fecha = fecha,
                temperatura = temperaturaPromedio,
                descripcion = descripcion
            )
        }
    }




}
