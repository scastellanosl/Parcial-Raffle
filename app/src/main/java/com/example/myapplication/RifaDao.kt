package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RifaDao {
    @Insert
    suspend fun insertar(rifa: Rifa)

    @Query("SELECT * FROM Rifa")
    suspend fun obtenerTodas(): List<Rifa>

    @Query("SELECT * FROM rifa")
    suspend fun getAll(): List<Rifa>

    @Query("SELECT * FROM rifa WHERE nombre = :nombre LIMIT 1")
    fun buscarPorNombre(nombre: String): Rifa?

    @Query("DELETE FROM rifa WHERE id = :rifaId")
    suspend fun eliminarPorId(rifaId: Int)  // Método para eliminar una rifa por su ID

}
