package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.DutyRecordEntity
import com.example.data.model.DutyRecord
import com.example.data.model.DutyStatus
import com.example.data.remote.FirebaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DutyRepository(
    private val database: AppDatabase,
    private val firebaseManager: FirebaseManager
) {
    private val dutyDao = database.dutyRecordDao()
    private val auditDao = database.auditDao()

    fun getRecordsForUser(userId: String): Flow<List<DutyRecord>> {
        return dutyDao.getRecordsForUser(userId).map { list -> list.map { it.toDomain() } }
    }

    fun getAllRecords(): Flow<List<DutyRecord>> {
        return dutyDao.getAllRecords().map { list -> list.map { it.toDomain() } }
    }

    suspend fun getRecordById(recordId: String): DutyRecord? = withContext(Dispatchers.IO) {
        dutyDao.getRecordById(recordId)?.toDomain()
    }

    suspend fun submitDuty(record: DutyRecord): Result<DutyRecord> = withContext(Dispatchers.IO) {
        try {
            val recordToSave = record.copy(
                status = DutyStatus.SUBMITTED,
                updatedAt = System.currentTimeMillis()
            )
            val entity = DutyRecordEntity.fromDomain(recordToSave)
            dutyDao.insert(entity)

            // Try syncing to Firestore
            val synced = firebaseManager.syncDutyRecordToFirestore(recordToSave)
            if (synced) {
                dutyDao.insert(entity.copy(isSynced = true))
            }

            // Audit Log
            auditDao.insert(
                AuditLogEntity(
                    userId = record.userId,
                    userName = record.employeeName,
                    action = "CREATE_DUTY",
                    details = "Submitted duty ${record.dutyNumber} (Bus: ${record.busNumber}, Income: ₹${record.income})",
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(recordToSave.copy(isSynced = synced))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveDraft(record: DutyRecord): Result<DutyRecord> = withContext(Dispatchers.IO) {
        try {
            val draftToSave = record.copy(
                status = DutyStatus.DRAFT,
                updatedAt = System.currentTimeMillis()
            )
            val entity = DutyRecordEntity.fromDomain(draftToSave)
            dutyDao.insert(entity)

            auditDao.insert(
                AuditLogEntity(
                    userId = record.userId,
                    userName = record.employeeName,
                    action = "SAVE_DRAFT",
                    details = "Saved draft for duty ${record.dutyNumber}",
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(draftToSave)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDuty(record: DutyRecord): Result<DutyRecord> = withContext(Dispatchers.IO) {
        try {
            val updated = record.copy(updatedAt = System.currentTimeMillis())
            val entity = DutyRecordEntity.fromDomain(updated)
            dutyDao.update(entity)

            val synced = firebaseManager.syncDutyRecordToFirestore(updated)
            if (synced) {
                dutyDao.update(entity.copy(isSynced = true))
            }

            auditDao.insert(
                AuditLogEntity(
                    userId = record.userId,
                    userName = record.employeeName,
                    action = "UPDATE_DUTY",
                    details = "Updated duty ${record.dutyNumber} (${record.recordId})",
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(updated.copy(isSynced = synced))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDuty(recordId: String, userId: String, userName: String, isAdmin: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = dutyDao.getRecordById(recordId)
                ?: return@withContext Result.failure(IllegalArgumentException("Record not found"))

            if (!isAdmin && existing.userId != userId) {
                return@withContext Result.failure(IllegalAccessException("You do not have permission to delete this record."))
            }

            dutyDao.delete(recordId)

            auditDao.insert(
                AuditLogEntity(
                    userId = userId,
                    userName = userName,
                    action = "DELETE_DUTY",
                    details = "Deleted duty record $recordId",
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncPendingRecords() = withContext(Dispatchers.IO) {
        val unsynced = dutyDao.getUnsyncedRecords()
        for (item in unsynced) {
            val synced = firebaseManager.syncDutyRecordToFirestore(item.toDomain())
            if (synced) {
                dutyDao.update(item.copy(isSynced = true))
            }
        }
    }
}
