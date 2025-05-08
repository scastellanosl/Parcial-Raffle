package com.example.myapplication

import android.os.Bundle
import java.util.Calendar
import android.app.DatePickerDialog
import android.content.Intent
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var rifaDao: RifaDao
    private lateinit var listViewRifas: ListView
    private lateinit var adapter: ArrayAdapter<String> // Adaptador para el ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_rifa)

        // Inicializamos la base de datos y el DAO
        rifaDao = AppDatabase.getDatabase(applicationContext).rifaDao()

        // Encontramos los elementos en el layout
        val etNombreRifa = findViewById<EditText>(R.id.editTextNombreRifa)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        listViewRifas = findViewById(R.id.listViewRifas)

        // Creamos un adaptador vacío inicialmente
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listViewRifas.adapter = adapter

        // Botón para crear una nueva rifa
        val buttonCrearRifa = findViewById<Button>(R.id.buttonCrearRifa)
        buttonCrearRifa.setOnClickListener {
            val nombreRifa = etNombreRifa.text.toString()
            val fecha = etFecha.text.toString()

            if (nombreRifa.isNotEmpty() && fecha.isNotEmpty()) {
                // Crear la nueva rifa en la base de datos
                val nuevaRifa = Rifa(nombre = nombreRifa, fecha = fecha)
                GlobalScope.launch(Dispatchers.IO) {
                    rifaDao.insertar(nuevaRifa)  // Guardamos la nueva rifa
                    loadRifas()  // Recargamos las rifas
                }
            }
        }

        // Configuración del DatePicker
        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val fechaSeleccionada = "%02d/%02d/%04d".format(d, m + 1, y)
                etFecha.setText(fechaSeleccionada)
            }, year, month, day)

            datePicker.show()
        }

        // Cargar rifas desde la base de datos
        loadRifas()

        val botonRegreso = Button(this).apply {
            text = "Volver al Menú"
            setOnClickListener {
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Ajustamos los parámetros de layout con margen
                val layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 100  // Ajustá este número como quieras
                }

        val layoutRaiz = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) as ViewGroup
        layoutRaiz.addView(botonRegreso, layoutParams)

    }


    // Función para cargar las rifas desde la base de datos y mostrarlas en el ListView
    private fun loadRifas() {
        GlobalScope.launch(Dispatchers.IO) {
            val rifas = rifaDao.getAll()  // Obtener todas las rifas
            val rifasWithCount = mutableListOf<String>()

            // Recorremos cada rifa para agregar la cantidad de participantes
            for (rifa in rifas) {
                // Obtener la cantidad de participantes
                val participantes = AppDatabase.getDatabase(applicationContext).participanteDao().obtenerPorRifa(rifa.id)
                val cantidadParticipantes = participantes.size

                // Formateamos el texto para incluir el nombre, fecha y cantidad de participantes
                rifasWithCount.add("${rifa.nombre} - ${rifa.fecha} (Participantes: $cantidadParticipantes)")
            }

            withContext(Dispatchers.Main) {
                // Actualizamos el adaptador con los datos nuevos
                adapter.clear()
                adapter.addAll(rifasWithCount)

                listViewRifas.setOnItemClickListener { _, _, position, _ ->
                    val rifaSeleccionada = rifas[position]
                    val intent = Intent(this@MainActivity, AgregarParticipanteActivity::class.java)
                    intent.putExtra("rifaId", rifaSeleccionada.id)
                    intent.putExtra("nombreRifa", rifaSeleccionada.nombre)
                    startActivity(intent)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        loadRifas() // Recarga la lista de rifas al volver a esta pantalla
    }


}



