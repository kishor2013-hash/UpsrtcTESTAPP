package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        DutyRecordEntity::class,
        AuditLogEntity::class,
        PortalSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun dutyRecordDao(): DutyRecordDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun portalSettingsDao(): PortalSettingsDao

    fun auditDao(): AuditLogDao = auditLogDao()
    fun settingsDao(): PortalSettingsDao = portalSettingsDao()

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "upsrtc_portal_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabase(context: Context): AppDatabase = getInstance(context)
    }
}
