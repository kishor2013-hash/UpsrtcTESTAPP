package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.DutyRecord
import com.example.data.model.DutyStatus
import com.example.data.model.EmployeeType
import com.example.data.model.User
import com.example.data.model.UserRole

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["employeeId"], unique = true),
        Index(value = ["mobile"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val fullName: String,
    val dob: String,
    val mobile: String,
    val employeeId: String,
    val employeeType: String,
    val depot: String,
    val depotCode: String,
    val designation: String,
    val role: String,
    val status: String,
    val photoUrl: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAt: Long
) {
    fun toDomain(): User {
        return User(
            uid = uid,
            email = email,
            fullName = fullName,
            dob = dob,
            mobile = mobile,
            employeeId = employeeId,
            employeeType = runCatching { EmployeeType.valueOf(employeeType) }.getOrDefault(EmployeeType.DRIVER),
            depot = depot,
            depotCode = depotCode,
            designation = designation,
            role = runCatching { UserRole.valueOf(role) }.getOrDefault(UserRole.DRIVER),
            status = status,
            photoUrl = photoUrl,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(user: User, passwordHash: String, passwordSalt: String): UserEntity {
            return UserEntity(
                uid = user.uid,
                email = user.email.trim().lowercase(),
                fullName = user.fullName.trim(),
                dob = user.dob,
                mobile = user.mobile.trim(),
                employeeId = user.employeeId.trim().uppercase(),
                employeeType = user.employeeType.name,
                depot = user.depot.trim().uppercase(),
                depotCode = user.depotCode.trim().uppercase(),
                designation = user.designation.trim(),
                role = user.role.name,
                status = user.status,
                photoUrl = user.photoUrl,
                passwordHash = passwordHash,
                passwordSalt = passwordSalt,
                createdAt = user.createdAt
            )
        }
    }
}

@Entity(
    tableName = "duty_records",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["dutyDate"]),
        Index(value = ["employeeId"])
    ]
)
data class DutyRecordEntity(
    @PrimaryKey
    val recordId: String,
    val userId: String,
    val employeeId: String,
    val employeeName: String,
    val employeeType: String,
    val depot: String,
    val dutyDate: String,
    val dutyNumber: String,
    val busNumber: String,
    val busRegistrationNumber: String,
    val route: String,
    val routeNumber: String,
    val startPoint: String,
    val endPoint: String,
    val dutyStartTime: String,
    val dutyEndTime: String,
    val shift: String,
    val totalKm: Double,
    val income: Double,
    val passengerCount: Int,
    val loadFactor: Double,
    val numberOfTrips: Int,
    val ticketCollection: Double,
    val cashCollection: Double,
    val remarks: String,
    val status: String,
    val isSynced: Boolean,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): DutyRecord {
        return DutyRecord(
            recordId = recordId,
            userId = userId,
            employeeId = employeeId,
            employeeName = employeeName,
            employeeType = employeeType,
            depot = depot,
            dutyDate = dutyDate,
            dutyNumber = dutyNumber,
            busNumber = busNumber,
            busRegistrationNumber = busRegistrationNumber,
            route = route,
            routeNumber = routeNumber,
            startPoint = startPoint,
            endPoint = endPoint,
            dutyStartTime = dutyStartTime,
            dutyEndTime = dutyEndTime,
            shift = shift,
            totalKm = totalKm,
            income = income,
            passengerCount = passengerCount,
            loadFactor = loadFactor,
            numberOfTrips = numberOfTrips,
            ticketCollection = ticketCollection,
            cashCollection = cashCollection,
            remarks = remarks,
            status = runCatching { DutyStatus.valueOf(status) }.getOrDefault(DutyStatus.SUBMITTED),
            isSynced = isSynced,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    companion object {
        fun fromDomain(record: DutyRecord): DutyRecordEntity {
            return DutyRecordEntity(
                recordId = record.recordId,
                userId = record.userId,
                employeeId = record.employeeId,
                employeeName = record.employeeName,
                employeeType = record.employeeType,
                depot = record.depot,
                dutyDate = record.dutyDate,
                dutyNumber = record.dutyNumber,
                busNumber = record.busNumber,
                busRegistrationNumber = record.busRegistrationNumber,
                route = record.route,
                routeNumber = record.routeNumber,
                startPoint = record.startPoint,
                endPoint = record.endPoint,
                dutyStartTime = record.dutyStartTime,
                dutyEndTime = record.dutyEndTime,
                shift = record.shift,
                totalKm = record.totalKm,
                income = record.income,
                passengerCount = record.passengerCount,
                loadFactor = record.loadFactor,
                numberOfTrips = record.numberOfTrips,
                ticketCollection = record.ticketCollection,
                cashCollection = record.cashCollection,
                remarks = record.remarks,
                status = record.status.name,
                isSynced = record.isSynced,
                createdAt = record.createdAt,
                updatedAt = record.updatedAt
            )
        }
    }
}

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val userName: String,
    val action: String,
    val details: String,
    val timestamp: Long
)

@Entity(tableName = "portal_settings")
data class PortalSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val paySlipUrl: String,
    val pfPortalUrl: String,
    val challanUrl: String,
    val manavSampadaUrl: String,
    val appName: String,
    val appVersion: String,
    val footerPartner: String,
    val footerTagline: String,
    val footerPhone: String,
    val footerWebsite: String
)
