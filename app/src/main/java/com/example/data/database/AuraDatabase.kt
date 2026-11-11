package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [InstalledAppEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {

    abstract fun installedAppDao(): InstalledAppDao

    companion object {
        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getDatabase(context: Context): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraDatabase::class.java,
                    "aura_launcher.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
