package com.example.myapplication


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Persona(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val edad: Int
)
