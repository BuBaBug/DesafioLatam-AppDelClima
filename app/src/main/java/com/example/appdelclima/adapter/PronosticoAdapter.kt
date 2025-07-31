package com.example.appdelclima.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.R
import com.example.appdelclima.model.DiaPronostico
import com.example.appdelclima.model.PronosticoResponse


class PronosticoAdapter(private var listaPronostico: List<DiaPronostico>) :
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

        // Mostrar sólo fecha (yyyy-MM-dd)
        holder.tvFecha.text = pronostico.fecha

        // Temperatura promedio (redondeada)
        holder.tvTemperatura.text = "🌡️ ${pronostico.temperatura.toInt()} °C"

        // Descripción capitalizada
        holder.tvDescripcion.text = pronostico.descripcion.replaceFirstChar { it.uppercaseChar() }
    }

    override fun getItemCount(): Int = listaPronostico.size

    fun actualizarDatos(nuevaLista: List<DiaPronostico>) {
        listaPronostico = nuevaLista
        notifyDataSetChanged()
    }

    // Función para procesar la lista de PronosticoItem y agrupar por día (5 días)
    private fun procesarPronostico(pronosticoResponse: PronosticoResponse): List<DiaPronostico> {
        val agrupado = pronosticoResponse.list.groupBy { it.dt_txt.substring(0, 10) } // Agrupa por fecha "yyyy-MM-dd"

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
