package com.example.myapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Rifa(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val fecha: String // puedes usar Date si prefieres, pero String es más simple para comenzar
)
