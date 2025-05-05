package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Persona::class, Rifa::class], version = 2) // Asegúrate de que la versión sea la correcta
abstract class AppDatabase : RoomDatabase() {

    abstract fun personaDao(): PersonaDao
    abstract fun rifaDao(): RifaDao

    // Definir la migración
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Definimos MIGRATION_1_2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Agregar lógica para modificar la base de datos
                database.execSQL("ALTER TABLE persona ADD COLUMN nueva_columna INTEGER DEFAULT 0 NOT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_basedatos.db"
                )
                    .addMigrations(MIGRATION_1_2) // Aquí agregamos la migración
                    .fallbackToDestructiveMigration() // Destruir la base de datos si la migración falla
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
