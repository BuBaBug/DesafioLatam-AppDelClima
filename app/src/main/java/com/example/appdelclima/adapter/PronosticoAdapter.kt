package com.example.appdelclima.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.R
import com.example.appdelclima.model.PronosticoItem
import java.text.SimpleDateFormat
import java.util.*

class PronosticoAdapter(private var listaPronostico: List<PronosticoItem>) :
    RecyclerView.Adapter<PronosticoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvTemperatura: TextView = view.findViewById(R.id.tvTemperatura)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_pronostico, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pronostico = listaPronostico[position]

        // Fecha (ejemplo: 2025-07-30 12:00:00 → Mar, 30 Jul 12:00)
        val formatoEntrada = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formatoSalida = SimpleDateFormat("EEE, dd MMM HH:mm", Locale.getDefault())
        val fecha = formatoEntrada.parse(pronostico.dt_txt)
        holder.tvFecha.text = fecha?.let { formatoSalida.format(it) } ?: pronostico.dt_txt

        // Temperatura en grados Celsius
        val temp = pronostico.main.temp
        holder.tvTemperatura.text = "🌡️ $temp °C"

        // Descripción del clima
        val descripcion = pronostico.weather.firstOrNull()?.description ?: "Sin descripción"
        holder.tvDescripcion.text = descripcion.replaceFirstChar { it.uppercaseChar() }
    }

    override fun getItemCount(): Int = listaPronostico.size

    fun actualizarDatos(nuevaLista: List<PronosticoItem>) {
        listaPronostico = nuevaLista
        notifyDataSetChanged()
    }
}
