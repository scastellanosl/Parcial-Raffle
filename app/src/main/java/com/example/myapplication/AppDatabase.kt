package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Persona::class, Rifa::class, Participante::class], version = 5)
abstract class AppDatabase : RoomDatabase() {

    abstract fun personaDao(): PersonaDao
    abstract fun rifaDao(): RifaDao
    abstract fun participanteDao(): ParticipanteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Lógica de migración 1 a 2
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Verificar si la tabla existe
                val tableExists = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='Participante'").use { cursor ->
                    cursor.count > 0
                }

                if (tableExists) {
                    // Si existe, crear una tabla temporal con la nueva estructura
                    database.execSQL("""
                        CREATE TABLE Participante_temp (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            nombre TEXT NOT NULL,
                            telefono TEXT NOT NULL,
                            rifaId INTEGER NOT NULL,
                            FOREIGN KEY (rifaId) REFERENCES Rifa(id) ON DELETE CASCADE
                        )
                    """)

                    // Copiar datos si es necesario (asumiendo que las columnas coinciden)
                    try {
                        database.execSQL("""
                            INSERT INTO Participante_temp (id, nombre, telefono, rifaId)
                            SELECT id, nombre, telefono, rifaId FROM Participante
                        """)
                    } catch (e: Exception) {
                        // Si falla la copia, ignoramos - probablemente era una tabla vacía o con estructura incompatible
                    }

                    // Eliminar la tabla original
                    database.execSQL("DROP TABLE Participante")

                    // Renombrar la tabla temporal
                    database.execSQL("ALTER TABLE Participante_temp RENAME TO Participante")
                } else {
                    // Si no existe, simplemente crear la tabla
                    database.execSQL("""
                        CREATE TABLE Participante (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            nombre TEXT NOT NULL,
                            telefono TEXT NOT NULL,
                            rifaId INTEGER NOT NULL,
                            FOREIGN KEY (rifaId) REFERENCES Rifa(id) ON DELETE CASCADE
                        )
                    """)
                }

                // Crear un índice para mejorar el rendimiento
                database.execSQL("CREATE INDEX IF NOT EXISTS index_Participante_rifaId ON Participante(rifaId)")
            }
        }
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Verificar si la tabla Ganador existe
                val tableExists = database.query("SELECT name FROM sqlite_master WHERE type='table' AND name='Ganador'").use { cursor ->
                    cursor.count > 0
                }

                if (tableExists) {
                    // Crear tabla temporal con la estructura deseada
                    database.execSQL("""
                CREATE TABLE Ganador_temp (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nombre TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    rifaId INTEGER NOT NULL,
                    fechaSeleccion INTEGER NOT NULL,
                    FOREIGN KEY (rifaId) REFERENCES Rifa(id) ON DELETE CASCADE
                )
            """)

                    // Intentar copiar datos desde la tabla anterior
                    try {
                        database.execSQL("""
                    INSERT INTO Ganador_temp (id, nombre, telefono, rifaId, fechaSeleccion)
                    SELECT id, nombre, telefono, rifaId, fechaSeleccion FROM Ganador
                """)
                    } catch (e: Exception) {
                        // Ignorar errores si la tabla estaba vacía o incompatible
                    }

                    // Eliminar la tabla original
                    database.execSQL("DROP TABLE Ganador")

                    // Renombrar la tabla temporal
                    database.execSQL("ALTER TABLE Ganador_temp RENAME TO Ganador")
                } else {
                    // Si no existía, crear directamente la tabla
                    database.execSQL("""
                CREATE TABLE Ganador (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nombre TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    rifaId INTEGER NOT NULL,
                    fechaSeleccion INTEGER NOT NULL,
                    FOREIGN KEY (rifaId) REFERENCES Rifa(id) ON DELETE CASCADE
                )
            """)
                }

                // Crear índice si necesitas (opcional)
                database.execSQL("CREATE INDEX IF NOT EXISTS index_Ganador_rifaId ON Ganador(rifaId)")
            }
        }


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mi_basedatos.db"
                )
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    // Considera eliminar esta línea si no quieres perder datos en caso de fallo
                    // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}