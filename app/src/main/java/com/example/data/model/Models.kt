package com.example.data.model

enum class EmployeeType {
    DRIVER,
    CONDUCTOR
}

enum class UserRole {
    SUPER_ADMIN,
    ADMIN,
    USER,
    DRIVER,
    CONDUCTOR,
    VIEWER
}

enum class DutyStatus {
    SUBMITTED,
    DRAFT
}

data class User(
    val uid: String,
    val email: String,
    val fullName: String,
    val dob: String = "",
    val mobile: String,
    val employeeId: String, // CND or DRV ID
    val employeeType: EmployeeType,
    val depot: String,
    val depotCode: String = "",
    val designation: String = "",
    val role: UserRole = UserRole.DRIVER,
    val status: String = "ACTIVE",
    val photoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class DutyRecord(
    val recordId: String,
    val userId: String,
    val employeeId: String,
    val employeeName: String,
    val employeeType: String,
    val depot: String,
    val dutyDate: String, // YYYY-MM-DD
    val dutyNumber: String,
    val busNumber: String,
    val busRegistrationNumber: String = "",
    val route: String,
    val routeNumber: String = "",
    val startPoint: String,
    val endPoint: String,
    val dutyStartTime: String,
    val dutyEndTime: String,
    val shift: String = "Morning", // Morning, Evening, Night
    val totalKm: Double,
    val income: Double,
    val passengerCount: Int = 0,
    val loadFactor: Double = 0.0,
    val numberOfTrips: Int = 1,
    val ticketCollection: Double = 0.0,
    val cashCollection: Double = 0.0,
    val remarks: String = "",
    val status: DutyStatus = DutyStatus.SUBMITTED,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class AuditLog(
    val id: Long = 0,
    val userId: String,
    val userName: String,
    val action: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PortalSettings(
    val id: Int = 1,
    val paySlipUrl: String = "https://payroll.mectoi.in/EmpLogin.aspx",
    val pfPortalUrl: String = "https://passbook.epfindia.gov.in/MemberPassBook/login",
    val challanUrl: String = "https://echallan.parivahan.gov.in/index/challan-print",
    val manavSampadaUrl: String = "https://ehrms.upsdc.gov.in/",
    val appName: String = "UPSRCTC ROADWAYS",
    val appVersion: String = "DIGITAL DUTY PORTAL V4.0",
    val footerPartner: String = "Grofasto Digital Solutions",
    val footerTagline: String = "GROW | CONNECT | SUCCEED.",
    val footerPhone: String = "+91 9457690255",
    val footerWebsite: String = "www.grofasto.com"
)
