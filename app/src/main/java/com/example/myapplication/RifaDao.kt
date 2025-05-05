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

}
