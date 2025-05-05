package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RifaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "personas.db"
    ).build()

    private val _rifas = MutableStateFlow<List<Rifa>>(emptyList())
    val rifas: StateFlow<List<Rifa>> = _rifas

    fun insertar(nombre: String, fecha: String) {
        viewModelScope.launch {
            db.rifaDao().insertar(Rifa(nombre = nombre, fecha = fecha))
            cargarRifas()
        }
    }

    fun cargarRifas() {
        viewModelScope.launch {
            _rifas.value = db.rifaDao().obtenerTodas()
        }
    }
}
