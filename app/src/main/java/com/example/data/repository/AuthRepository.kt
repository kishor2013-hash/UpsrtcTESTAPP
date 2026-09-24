package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.UserEntity
import com.example.data.model.EmployeeType
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.remote.FirebaseManager
import com.example.data.util.SecurityUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

data class AdminStats(
    val totalUsers: Int = 0,
    val totalDrivers: Int = 0,
    val totalConductors: Int = 0,
    val totalDuties: Int = 0,
    val totalDepots: Int = 0
)

class AuthRepository(
    private val context: Context,
    private val database: AppDatabase,
    private val firebaseManager: FirebaseManager
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("upsrtc_auth_prefs", Context.MODE_PRIVATE)
    private val userDao = database.userDao()
    private val auditDao = database.auditDao()
    private val dutyDao = database.dutyRecordDao()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val repoScope = CoroutineScope(Dispatchers.IO)

    init {
        // Check saved session
        val savedUid = prefs.getString("session_uid", null)
        if (!savedUid.isNullOrBlank()) {
            repoScope.launch {
                val userEntity = userDao.getUserById(savedUid)
                if (userEntity != null && userEntity.status == "ACTIVE") {
                    _currentUser.value = userEntity.toDomain()
                } else {
                    prefs.edit().remove("session_uid").apply()
                }
            }
        }
    }

    suspend fun register(
        fullName: String,
        dob: String,
        mobile: String,
        email: String,
        password: String,
        employeeId: String,
        employeeType: EmployeeType,
        depot: String,
        depotCode: String,
        designation: String
    ): Result<User> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val cleanMobile = mobile.trim()
        val cleanEmpId = employeeId.trim().uppercase()

        // Validations
        if (!SecurityUtils.isValidEmail(cleanEmail)) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        if (!SecurityUtils.isValidMobile(cleanMobile)) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid 10-digit mobile number."))
        }
        if (!SecurityUtils.isValidEmployeeId(cleanEmpId)) {
            return@withContext Result.failure(IllegalArgumentException("Employee ID / CND must be at least 3 characters."))
        }
        if (password.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        // Duplicate checks
        if (userDao.getUserByEmail(cleanEmail) != null) {
            return@withContext Result.failure(IllegalStateException("An account with this email already exists."))
        }
        if (userDao.getUserByMobile(cleanMobile) != null) {
            return@withContext Result.failure(IllegalStateException("An account with this mobile number already exists."))
        }
        if (userDao.getUserByEmployeeId(cleanEmpId) != null) {
            return@withContext Result.failure(IllegalStateException("An account with this Employee ID / CND already exists."))
        }

        // Firebase Auth account creation if available
        var uid = UUID.randomUUID().toString()
        val auth = firebaseManager.auth
        if (auth != null) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
                authResult.user?.uid?.let { uid = it }
            } catch (e: Exception) {
                // If Firebase fails (e.g. offline or not fully configured), proceed with local secure registration
            }
        }

        // Generate password salt and hash
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password, salt)

        val user = User(
            uid = uid,
            email = cleanEmail,
            fullName = fullName.trim(),
            dob = dob.trim(),
            mobile = cleanMobile,
            employeeId = cleanEmpId,
            employeeType = employeeType,
            depot = depot.trim(),
            depotCode = depotCode.trim(),
            designation = designation.trim().ifBlank { if (employeeType == EmployeeType.DRIVER) "Bus Driver" else "Bus Conductor" },
            role = if (userDao.count() == 0) UserRole.ADMIN else if (employeeType == EmployeeType.DRIVER) UserRole.DRIVER else UserRole.CONDUCTOR,
            status = "ACTIVE",
            createdAt = System.currentTimeMillis()
        )

        val entity = UserEntity.fromDomain(user, hash, salt)
        userDao.insert(entity)

        // Try syncing to Firestore
        firebaseManager.syncUserProfileToFirestore(user)

        // Audit log
        auditDao.insert(
            AuditLogEntity(
                userId = user.uid,
                userName = user.fullName,
                action = "SIGNUP",
                details = "Registered as ${user.employeeType.name} with ID ${user.employeeId}",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(user)
    }

    suspend fun login(
        loginId: String,
        password: String,
        rememberMe: Boolean
    ): Result<User> = withContext(Dispatchers.IO) {
        val identifier = loginId.trim()
        if (identifier.isBlank() || password.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter your Login ID / Email and password."))
        }

        // User can log in with Email, Mobile, or Employee ID / CND
        val entity = userDao.getUserByEmail(identifier)
            ?: userDao.getUserByMobile(identifier)
            ?: userDao.getUserByEmployeeId(identifier)

        if (entity == null) {
            return@withContext Result.failure(IllegalArgumentException("Invalid credentials. No employee record found."))
        }

        if (entity.status != "ACTIVE") {
            return@withContext Result.failure(IllegalStateException("Your account is currently disabled. Please contact depot administration."))
        }

        // Verify password
        val isPasswordValid = SecurityUtils.verifyPassword(password, entity.passwordSalt, entity.passwordHash)
        if (!isPasswordValid) {
            return@withContext Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        // Firebase Auth login attempt if configured
        val auth = firebaseManager.auth
        if (auth != null) {
            try {
                auth.signInWithEmailAndPassword(entity.email, password).await()
            } catch (e: Exception) {
                // If offline or Firebase fails, local verification succeeded
            }
        }

        val user = entity.toDomain()
        _currentUser.value = user

        if (rememberMe) {
            prefs.edit().putString("session_uid", user.uid).apply()
        } else {
            prefs.edit().remove("session_uid").apply()
        }

        // Audit log
        auditDao.insert(
            AuditLogEntity(
                userId = user.uid,
                userName = user.fullName,
                action = "LOGIN",
                details = "Logged in from ${user.depot}",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success(user)
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        val user = _currentUser.value
        if (user != null) {
            auditDao.insert(
                AuditLogEntity(
                    userId = user.uid,
                    userName = user.fullName,
                    action = "LOGOUT",
                    details = "User logged out",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        firebaseManager.auth?.signOut()
        prefs.edit().remove("session_uid").apply()
        _currentUser.value = null
    }

    suspend fun resetPassword(emailOrEmpId: String, newPass: String): Result<String> = withContext(Dispatchers.IO) {
        val id = emailOrEmpId.trim()
        val entity = userDao.getUserByEmail(id) ?: userDao.getUserByEmployeeId(id)
            ?: return@withContext Result.failure(IllegalArgumentException("No registered employee found with this identifier."))

        if (newPass.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        }

        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newPass, newSalt)
        val updatedEntity = entity.copy(passwordHash = newHash, passwordSalt = newSalt)
        userDao.update(updatedEntity)

        auditDao.insert(
            AuditLogEntity(
                userId = entity.uid,
                userName = entity.fullName,
                action = "PASSWORD_RESET",
                details = "Password was reset successfully",
                timestamp = System.currentTimeMillis()
            )
        )

        Result.success("Password updated successfully. Please login with your new password.")
    }

    suspend fun updateProfile(updatedUser: User): Result<Unit> = withContext(Dispatchers.IO) {
        val entity = userDao.getUserById(updatedUser.uid)
            ?: return@withContext Result.failure(IllegalStateException("User not found."))

        val newEntity = entity.copy(
            fullName = updatedUser.fullName,
            mobile = updatedUser.mobile,
            depot = updatedUser.depot,
            depotCode = updatedUser.depotCode,
            designation = updatedUser.designation,
            dob = updatedUser.dob,
            photoUrl = updatedUser.photoUrl
        )
        userDao.update(newEntity)
        _currentUser.value = newEntity.toDomain()
        firebaseManager.syncUserProfileToFirestore(newEntity.toDomain())

        auditDao.insert(
            AuditLogEntity(
                userId = updatedUser.uid,
                userName = updatedUser.fullName,
                action = "PROFILE_UPDATE",
                details = "Updated personal profile information",
                timestamp = System.currentTimeMillis()
            )
        )
        Result.success(Unit)
    }

    fun getAllUsersFlow(): Flow<List<User>> {
        return userDao.getAllUsersFlow().map { list -> list.map { it.toDomain() } }
    }

    fun getAllUsers(): Flow<List<User>> = getAllUsersFlow()

    suspend fun updateUserProfile(updatedUser: User): Result<Unit> = updateProfile(updatedUser)

    suspend fun updateUserStatus(uid: String, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        val entity = userDao.getUserById(uid) ?: return@withContext Result.failure(IllegalStateException("User not found"))
        userDao.update(entity.copy(status = status))
        Result.success(Unit)
    }

    suspend fun updateUserRole(uid: String, role: UserRole): Result<Unit> = withContext(Dispatchers.IO) {
        val entity = userDao.getUserById(uid) ?: return@withContext Result.failure(IllegalStateException("User not found"))
        userDao.update(entity.copy(role = role.name))
        Result.success(Unit)
    }

    suspend fun getAdminStats(): AdminStats = withContext(Dispatchers.IO) {
        AdminStats(
            totalUsers = userDao.count(),
            totalDrivers = userDao.countDrivers(),
            totalConductors = userDao.countConductors(),
            totalDuties = dutyDao.countTotalDuties(),
            totalDepots = userDao.countDepots()
        )
    }
}
