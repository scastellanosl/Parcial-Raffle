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
        var numerosOcupados by remember { mutableStateOf(setOf<Int>()) }
        var rifaEncontrada by remember { mutableStateOf<Rifa?>(null) }

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
                // Buscar en base de datos
                GlobalScope.launch(Dispatchers.IO) {
                    val rifaDao = AppDatabase.getDatabase(applicationContext).rifaDao()
                    val participanteDao = AppDatabase.getDatabase(applicationContext).participanteDao()

                    val rifa = rifaDao.buscarPorNombre(nombreBuscado)
                    val ocupados = if (rifa != null) {
                        val participantes = participanteDao.obtenerPorRifa(rifa.id)
                        participantes.map { it.numero }.toSet()
                    } else {
                        emptySet()
                    }

                    withContext(Dispatchers.Main) {
                        rifaEncontrada = rifa
                        numerosOcupados = ocupados
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

                LazyVerticalGrid(
                    columns = GridCells.Fixed(10),
                    contentPadding = PaddingValues(4.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(100) { numero ->
                        val ocupado = numerosOcupados.contains(numero)
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(36.dp)
                                .background(if (ocupado) Color.Gray else Color.Green),
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

            // Botón de salida al menú: MenuActivity

        }

    }
}
