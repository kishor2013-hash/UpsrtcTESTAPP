package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.DutyRecord
import com.example.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseManager(private val context: Context) {
    private val tag = "FirebaseManager"

    val isFirebaseAvailable: Boolean
        get() {
            return try {
                FirebaseApp.getInstance() != null
            } catch (e: Exception) {
                false
            }
        }

    val auth: FirebaseAuth?
        get() {
            return try {
                if (isFirebaseAvailable) FirebaseAuth.getInstance() else null
            } catch (e: Exception) {
                Log.w(tag, "FirebaseAuth not initialized: ${e.message}")
                null
            }
        }

    val firestore: FirebaseFirestore?
        get() {
            return try {
                if (isFirebaseAvailable) FirebaseFirestore.getInstance() else null
            } catch (e: Exception) {
                Log.w(tag, "FirebaseFirestore not initialized: ${e.message}")
                null
            }
        }

    suspend fun syncDutyRecordToFirestore(record: DutyRecord): Boolean {
        val db = firestore ?: return false
        return try {
            val recordMap = hashMapOf(
                "recordId" to record.recordId,
                "userId" to record.userId,
                "employeeId" to record.employeeId,
                "employeeName" to record.employeeName,
                "employeeType" to record.employeeType,
                "depot" to record.depot,
                "dutyDate" to record.dutyDate,
                "dutyNumber" to record.dutyNumber,
                "busNumber" to record.busNumber,
                "busRegistrationNumber" to record.busRegistrationNumber,
                "route" to record.route,
                "routeNumber" to record.routeNumber,
                "startPoint" to record.startPoint,
                "endPoint" to record.endPoint,
                "dutyStartTime" to record.dutyStartTime,
                "dutyEndTime" to record.dutyEndTime,
                "shift" to record.shift,
                "totalKm" to record.totalKm,
                "income" to record.income,
                "passengerCount" to record.passengerCount,
                "loadFactor" to record.loadFactor,
                "numberOfTrips" to record.numberOfTrips,
                "ticketCollection" to record.ticketCollection,
                "cashCollection" to record.cashCollection,
                "remarks" to record.remarks,
                "status" to record.status.name,
                "createdAt" to record.createdAt,
                "updatedAt" to record.updatedAt
            )
            db.collection("duty_records").document(record.recordId).set(recordMap).await()
            true
        } catch (e: Exception) {
            Log.w(tag, "Error syncing duty record to Firestore: ${e.message}")
            false
        }
    }

    suspend fun syncUserProfileToFirestore(user: User): Boolean {
        val db = firestore ?: return false
        return try {
            val userMap = hashMapOf(
                "uid" to user.uid,
                "email" to user.email,
                "fullName" to user.fullName,
                "dob" to user.dob,
                "mobile" to user.mobile,
                "employeeId" to user.employeeId,
                "employeeType" to user.employeeType.name,
                "depot" to user.depot,
                "depotCode" to user.depotCode,
                "designation" to user.designation,
                "role" to user.role.name,
                "status" to user.status,
                "photoUrl" to user.photoUrl,
                "createdAt" to user.createdAt
            )
            db.collection("users").document(user.uid).set(userMap).await()
            true
        } catch (e: Exception) {
            Log.w(tag, "Error syncing user profile to Firestore: ${e.message}")
            false
        }
    }
}
