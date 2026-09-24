package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.model.AuditLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuditRepository(private val database: AppDatabase) {
    private val auditDao = database.auditDao()

    fun getRecentLogs(limit: Int = 100): Flow<List<AuditLog>> {
        return auditDao.getRecentLogs(limit).map { list: List<AuditLogEntity> ->
            list.map { entity: AuditLogEntity ->
                AuditLog(
                    id = entity.id,
                    userId = entity.userId,
                    userName = entity.userName,
                    action = entity.action,
                    details = entity.details,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    suspend fun log(userId: String, userName: String, action: String, details: String) = withContext(Dispatchers.IO) {
        try {
            auditDao.insert(
                AuditLogEntity(
                    userId = userId,
                    userName = userName,
                    action = action,
                    details = details,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (_: Exception) {}
    }
}
