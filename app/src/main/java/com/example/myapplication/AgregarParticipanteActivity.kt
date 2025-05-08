package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AgregarParticipanteActivity : ComponentActivity() {

    // No necesitamos lateinit para el rifaId, ya que lo pasamos como parámetro
    private var rifaId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el rifaId del intent
        rifaId = intent.getIntExtra("rifaId", -1)

        setContent {
            val context = LocalContext.current // Obtener el contexto correctamente
            AgregarParticipanteScreen(rifaId, context)
        }
    }
}

@Composable
fun AgregarParticipanteScreen(rifaId: Int, context: Context) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }
    var cantidadParticipantes by remember { mutableStateOf(0) }

    // Inicializar la base de datos
    val participanteDao = remember { AppDatabase.getDatabase(context).participanteDao() }

    // Conozco la cantidad de participantes de la rifa
    LaunchedEffect(rifaId) {
        GlobalScope.launch {
            val participantes = participanteDao.obtenerPorRifa(rifaId)
            cantidadParticipantes = participantes.size
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Rifa ID: $rifaId", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Cantidad de Participantes: $cantidadParticipantes")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = numero,
            onValueChange = { numero = it },
            label = { Text("Número (0-99)") },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {}
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (mensajeError.isNotEmpty()) {
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Validar si los campos están completos
                if (nombre.isNotEmpty() && telefono.isNotEmpty() && numero.isNotEmpty() && rifaId != -1) {
                    GlobalScope.launch {
                        // Verificar si el número ya está ocupado
                        val participanteConNumero = participanteDao.obtenerPorRifa(rifaId).find { it.numero == numero.toInt() }

                        if (participanteConNumero != null) {
                            // Si el número está ocupado, mostrar error
                            mensajeError = "Este número ya está ocupado"
                        } else {
                            // Creando el participante
                            val participante = Participante(
                                nombre = nombre,
                                telefono = telefono,
                                rifaId = rifaId,
                                numero = numero.toInt() // Añadir el número al participante
                            )
                            participanteDao.insertar(participante)
                            // Limpiar los campos cuando se agregue un nuevo participante
                            nombre = ""
                            telefono = ""
                            numero = ""
                            mensajeError = ""
                            Toast.makeText(
                                context,
                                "Participante guardado",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    mensajeError = "Completa todos los campos"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Participante")
        }

        // Botón para eliminar la rifa

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                GlobalScope.launch {
                    // Borrar los participantes primero (si existen)
                    participanteDao.eliminarPorRifa(rifaId)

                    // Borrar la rifa en sí
                    AppDatabase.getDatabase(context).rifaDao().eliminarPorId(rifaId)

                    // Volver al activity anterior (cerrar este)
                    (context as? ComponentActivity)?.finish()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Borrar Rifa", color = MaterialTheme.colorScheme.onError)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AgregarParticipanteScreen(rifaId = 1, context = LocalContext.current)
}

