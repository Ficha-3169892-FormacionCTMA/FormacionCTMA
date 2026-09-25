package com.miguelloaiza.miformacionctma.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ActividadEntity::class, EvidenciaEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun actividadDao(): ActividadDao
    abstract fun evidenciaDao(): EvidenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerInstancia(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miformacionctma_db"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instancia
                instancia
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS evidencias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        actividadId INTEGER NOT NULL,
                        uri TEXT NOT NULL,
                        mimeType TEXT NOT NULL,
                        tamanoBytes INTEGER NOT NULL,
                        nombre TEXT NOT NULL,
                        estado TEXT NOT NULL
                    )""".trimIndent()
                )
                database.execSQL("CREATE INDEX IF NOT EXISTS index_evidencias_actividadId ON evidencias(actividadId)")
            }
        }
    }
}
