package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MenuScreen(
                        onBuscarClick = {
                            startActivity(Intent(this, BuscarRifaActivity::class.java))
                        },
                        onCrearClick = {
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    onBuscarClick: () -> Unit,
    onCrearClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onBuscarClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Buscar Rifa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onCrearClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Crear Rifa")
        }
    }
}
