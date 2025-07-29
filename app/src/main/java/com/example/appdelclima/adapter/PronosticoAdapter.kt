package com.example.appdelclima.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdelclima.R
import com.example.appdelclima.model.DiaPronostico

class PronosticoAdapter(
    private var listaPronostico: List<DiaPronostico>
) : RecyclerView.Adapter<PronosticoAdapter.PronosticoViewHolder>() {

    class PronosticoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val tvTemperatura: TextView = view.findViewById(R.id.tvTemperatura)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PronosticoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dia_pronostico, parent, false)
        return PronosticoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PronosticoViewHolder, position: Int) {
        val item = listaPronostico[position]
        holder.tvFecha.text = item.fecha
        holder.tvTemperatura.text = "Temp: ${item.temperatura}°C"
        holder.tvDescripcion.text = item.descripcion
    }

    override fun getItemCount(): Int = listaPronostico.size

    fun actualizarDatos(nuevaLista: List<DiaPronostico>) {
        listaPronostico = nuevaLista
        notifyDataSetChanged()
    }
}
