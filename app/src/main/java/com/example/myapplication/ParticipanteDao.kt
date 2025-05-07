package com.example.myapplication
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ParticipanteDao {
    @Insert
    suspend fun insertar(participante: Participante)

    @Query("SELECT * FROM Participante WHERE rifaId = :rifaId")
    suspend fun obtenerPorRifa(rifaId: Int): List<Participante>
}
