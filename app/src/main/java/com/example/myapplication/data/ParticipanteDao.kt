package com.example.myapplication.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.Participante

@Dao
interface ParticipanteDao {
    @Insert
    suspend fun insertar(participante: Participante)

    @Query("SELECT * FROM Participante WHERE rifaId = :rifaId")
    suspend fun obtenerPorRifa(rifaId: Int): List<Participante>

    @Query("DELETE FROM participante WHERE rifaId = :rifaId")
    suspend fun eliminarPorRifa(rifaId: Int)  // Método para eliminar todos los participantes por rifaId

}
