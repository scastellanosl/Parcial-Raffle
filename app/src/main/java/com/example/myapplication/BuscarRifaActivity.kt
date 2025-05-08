package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuscarRifaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BuscarRifaScreen()
                }
            }
        }
    }

    @Composable
    fun BuscarRifaScreen() {
        var nombreBuscado by remember { mutableStateOf("") }
        var participantes by remember { mutableStateOf(listOf<Participante>()) }
        var rifaEncontrada by remember { mutableStateOf<Rifa?>(null) }
        var participanteSeleccionado by remember { mutableStateOf<Participante?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = nombreBuscado,
                onValueChange = { nombreBuscado = it },
                label = { Text("Nombre de la rifa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = {
                GlobalScope.launch(Dispatchers.IO) {
                    val rifaDao = AppDatabase.getDatabase(applicationContext).rifaDao()
                    val participanteDao = AppDatabase.getDatabase(applicationContext).participanteDao()

                    val rifa = rifaDao.buscarPorNombre(nombreBuscado)
                    val lista = if (rifa != null) {
                        participanteDao.obtenerPorRifa(rifa.id)
                    } else emptyList()

                    withContext(Dispatchers.Main) {
                        rifaEncontrada = rifa
                        participantes = lista
                    }
                }
            }) {
                Text("Buscar")
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (rifaEncontrada != null) {
                Text(
                    text = "Rifa: ${rifaEncontrada!!.nombre} - ${rifaEncontrada!!.fecha}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                val ocupados = participantes.map { it.numero }.toSet()

                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(100) { numero ->
                        val ocupado = ocupados.contains(numero)
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(36.dp)
                                .background(if (ocupado) Color.Gray else Color.Green)
                                .clickable(enabled = ocupado) {
                                    participanteSeleccionado =
                                        participantes.firstOrNull { it.numero == numero }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = numero.toString(),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Text("No se ha encontrado ninguna rifa con ese nombre.")
            }
        }

        // Diálogo para mostrar la información del participante
        if (participanteSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { participanteSeleccionado = null },
                confirmButton = {
                    TextButton(onClick = { participanteSeleccionado = null }) {
                        Text("Cerrar")
                    }
                },
                title = { Text("Información del Participante") },
                text = {
                    Text("Nombre: ${participanteSeleccionado!!.nombre}\nTeléfono: ${participanteSeleccionado!!.telefono}")
                }
            )
        }
    }

}
