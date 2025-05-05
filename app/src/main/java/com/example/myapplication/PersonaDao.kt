package com.example.myapplication

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PersonaDao {
    @Insert
    suspend fun insertar(persona: Persona)

    @Query("SELECT * FROM Persona")
    suspend fun obtenerTodos(): List<Persona>
}
