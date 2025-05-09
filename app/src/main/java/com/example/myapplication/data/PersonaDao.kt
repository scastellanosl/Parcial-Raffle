package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.Persona

@Dao
interface PersonaDao {
    @Insert
    suspend fun insertar(persona: Persona)

    @Query("SELECT * FROM Persona")
    suspend fun obtenerTodos(): List<Persona>
}
