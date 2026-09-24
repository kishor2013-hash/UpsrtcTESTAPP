package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE UPPER(employeeId) = UPPER(:employeeId) LIMIT 1")
    suspend fun getUserByEmployeeId(employeeId: String): UserEntity?

    @Query("SELECT * FROM users WHERE mobile = :mobile LIMIT 1")
    suspend fun getUserByMobile(mobile: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM users WHERE employeeType = 'DRIVER'")
    suspend fun countDrivers(): Int

    @Query("SELECT COUNT(*) FROM users WHERE employeeType = 'CONDUCTOR'")
    suspend fun countConductors(): Int

    @Query("SELECT COUNT(DISTINCT depot) FROM users WHERE depot != ''")
    suspend fun countDepots(): Int
}

@Dao
interface DutyRecordDao {
    @Query("SELECT * FROM duty_records WHERE userId = :userId ORDER BY dutyDate DESC, createdAt DESC")
    fun getRecordsForUser(userId: String): Flow<List<DutyRecordEntity>>

    @Query("SELECT * FROM duty_records ORDER BY dutyDate DESC, createdAt DESC")
    fun getAllRecords(): Flow<List<DutyRecordEntity>>

    @Query("SELECT * FROM duty_records WHERE recordId = :recordId LIMIT 1")
    suspend fun getRecordById(recordId: String): DutyRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: DutyRecordEntity)

    @Update
    suspend fun update(record: DutyRecordEntity)

    @Query("DELETE FROM duty_records WHERE recordId = :recordId")
    suspend fun delete(recordId: String)

    @Query("SELECT COUNT(*) FROM duty_records")
    suspend fun countTotalDuties(): Int

    @Query("SELECT * FROM duty_records WHERE isSynced = 0")
    suspend fun getUnsyncedRecords(): List<DutyRecordEntity>
}

@Dao
interface AuditLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int = 100): Flow<List<AuditLogEntity>>
}

@Dao
interface PortalSettingsDao {
    @Query("SELECT * FROM portal_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<PortalSettingsEntity?>

    @Query("SELECT * FROM portal_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): PortalSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: PortalSettingsEntity)
}
