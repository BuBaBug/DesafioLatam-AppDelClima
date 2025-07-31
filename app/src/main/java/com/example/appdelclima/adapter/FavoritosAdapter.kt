package com.example.appdelclima.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FavoritosAdapter(
    private var listaFavoritos: List<String>,
    private val onCiudadClick: (String) -> Unit
) : RecyclerView.Adapter<FavoritosAdapter.FavoritoViewHolder>() {

    inner class FavoritoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCiudad: TextView = view.findViewById(android.R.id.text1)
        init {
            view.setOnClickListener {
                val posicion = adapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    onCiudadClick(listaFavoritos[posicion])
                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return FavoritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritoViewHolder, position: Int) {
        holder.tvCiudad.text = listaFavoritos[position]
    }

    override fun getItemCount(): Int = listaFavoritos.size

    fun actualizarLista(nuevaLista: List<String>) {
        listaFavoritos = nuevaLista
        notifyDataSetChanged()
    }

}
