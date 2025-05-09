package com.example.myapplication

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Rifa::class,
            parentColumns = ["id"],
            childColumns = ["rifaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("rifaId")] // Agregar índice a la columna rifaId para mejorar rendimiento
)
data class Participante(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val numero: Int,
    val rifaId: Int
)