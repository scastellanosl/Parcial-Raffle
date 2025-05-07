package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParticipanteAdapter(private var participantes: List<Participante>) : RecyclerView.Adapter<ParticipanteAdapter.ParticipanteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipanteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participante, parent, false)
        return ParticipanteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipanteViewHolder, position: Int) {
        val participante = participantes[position]
        holder.bind(participante)
    }

    override fun getItemCount(): Int = participantes.size

    inner class ParticipanteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumero: TextView = itemView.findViewById(R.id.tvNumero)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)

        fun bind(participante: Participante) {
            tvNumero.text = (adapterPosition + 1).toString() // Mostrar el número del participante
            tvNombre.text = participante.nombre
            tvTelefono.text = participante.telefono
        }
    }

    fun setParticipantes(participantes: List<Participante>) {
        this.participantes = participantes
        notifyDataSetChanged()
    }
}
