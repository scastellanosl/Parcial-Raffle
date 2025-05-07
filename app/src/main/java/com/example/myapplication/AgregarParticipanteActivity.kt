package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AgregarParticipanteActivity : AppCompatActivity() {

    private lateinit var participanteDao: ParticipanteDao
    private lateinit var adapter: ParticipanteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_participante)

        val nombreRifa = intent.getStringExtra("nombreRifa")
        val rifaId = intent.getIntExtra("rifaId", -1)

        participanteDao = AppDatabase.getDatabase(applicationContext).participanteDao()

        findViewById<TextView>(R.id.textViewNombreRifa).text = "Rifa: $nombreRifa"

        // Establecer un valor inicial de participantes
        val cantidadParticipantesTextView = findViewById<TextView>(R.id.textViewCantidadParticipantes)

        val nombreParticipante = findViewById<EditText>(R.id.editTextNombreParticipante)
        val telefonoParticipante = findViewById<EditText>(R.id.editTextTelefonoParticipante)
        val btnGuardar = findViewById<Button>(R.id.buttonGuardarParticipante)

        adapter = ParticipanteAdapter(emptyList())
        val recyclerView = findViewById<RecyclerView>(R.id.rvParticipantes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnGuardar.setOnClickListener {
            val nombre = nombreParticipante.text.toString()
            val telefono = telefonoParticipante.text.toString()

            if (nombre.isNotEmpty() && telefono.isNotEmpty() && rifaId != -1) {
                val participante = Participante(nombre = nombre, telefono = telefono, rifaId = rifaId)

                GlobalScope.launch {
                    participanteDao.insertar(participante)
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Participante guardado", Toast.LENGTH_SHORT).show()
                        loadParticipantes(rifaId)
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Cargar la cantidad de participantes de la rifa
        loadParticipantes(rifaId)
        loadCantidadParticipantes(rifaId, cantidadParticipantesTextView)
    }

    private fun loadParticipantes(rifaId: Int) {
        GlobalScope.launch {
            val participantes = participanteDao.obtenerPorRifa(rifaId)
            runOnUiThread {
                adapter.setParticipantes(participantes)
            }
        }
    }

    private fun loadCantidadParticipantes(rifaId: Int, cantidadParticipantesTextView: TextView) {
        GlobalScope.launch {
            val cantidad = participanteDao.obtenerPorRifa(rifaId).size
            runOnUiThread {
                cantidadParticipantesTextView.text = "Cantidad de Participantes: $cantidad"
            }
        }
    }
}


