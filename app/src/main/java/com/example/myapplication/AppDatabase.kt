package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Persona::class, Rifa::class, Participante::class],
    version = 1, // Si haces cambios en el futuro, aumenta esta versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personaDao(): PersonaDao
    abstract fun rifaDao(): RifaDao
    abstract fun participanteDao(): ParticipanteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_basedatos.db"
                )
                    .fallbackToDestructiveMigration() // Borra y recrea la DB si cambia la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
