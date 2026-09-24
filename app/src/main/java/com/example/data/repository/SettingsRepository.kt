package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.PortalSettingsEntity
import com.example.data.model.PortalSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepository(private val database: AppDatabase) {
    private val settingsDao = database.portalSettingsDao()
    private val auditDao = database.auditDao()

    fun getSettings(): Flow<PortalSettings> {
        return settingsDao.getSettings().map { entity ->
            if (entity != null) {
                PortalSettings(
                    id = entity.id,
                    paySlipUrl = entity.paySlipUrl,
                    pfPortalUrl = entity.pfPortalUrl,
                    challanUrl = entity.challanUrl,
                    manavSampadaUrl = entity.manavSampadaUrl,
                    appName = entity.appName,
                    appVersion = entity.appVersion,
                    footerPartner = entity.footerPartner,
                    footerTagline = entity.footerTagline,
                    footerPhone = entity.footerPhone,
                    footerWebsite = entity.footerWebsite
                )
            } else {
                PortalSettings()
            }
        }
    }

    suspend fun getSettingsSync(): PortalSettings = withContext(Dispatchers.IO) {
        val entity = settingsDao.getSettingsSync()
        if (entity != null) {
            PortalSettings(
                id = entity.id,
                paySlipUrl = entity.paySlipUrl,
                pfPortalUrl = entity.pfPortalUrl,
                challanUrl = entity.challanUrl,
                manavSampadaUrl = entity.manavSampadaUrl,
                appName = entity.appName,
                appVersion = entity.appVersion,
                footerPartner = entity.footerPartner,
                footerTagline = entity.footerTagline,
                footerPhone = entity.footerPhone,
                footerWebsite = entity.footerWebsite
            )
        } else {
            val defaultSettings = PortalSettings()
            settingsDao.insertOrUpdate(
                PortalSettingsEntity(
                    id = defaultSettings.id,
                    paySlipUrl = defaultSettings.paySlipUrl,
                    pfPortalUrl = defaultSettings.pfPortalUrl,
                    challanUrl = defaultSettings.challanUrl,
                    manavSampadaUrl = defaultSettings.manavSampadaUrl,
                    appName = defaultSettings.appName,
                    appVersion = defaultSettings.appVersion,
                    footerPartner = defaultSettings.footerPartner,
                    footerTagline = defaultSettings.footerTagline,
                    footerPhone = defaultSettings.footerPhone,
                    footerWebsite = defaultSettings.footerWebsite
                )
            )
            defaultSettings
        }
    }

    suspend fun updateSettings(settings: PortalSettings): Result<Unit> = updateSettings(settings, "system", "Administrator")

    suspend fun updateSettings(settings: PortalSettings, adminUserId: String, adminName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            settingsDao.insertOrUpdate(
                PortalSettingsEntity(
                    id = 1,
                    paySlipUrl = settings.paySlipUrl.trim(),
                    pfPortalUrl = settings.pfPortalUrl.trim(),
                    challanUrl = settings.challanUrl.trim(),
                    manavSampadaUrl = settings.manavSampadaUrl.trim(),
                    appName = settings.appName.trim(),
                    appVersion = settings.appVersion.trim(),
                    footerPartner = settings.footerPartner.trim(),
                    footerTagline = settings.footerTagline.trim(),
                    footerPhone = settings.footerPhone.trim(),
                    footerWebsite = settings.footerWebsite.trim()
                )
            )

            auditDao.insert(
                AuditLogEntity(
                    userId = adminUserId,
                    userName = adminName,
                    action = "SETTINGS_UPDATE",
                    details = "Updated portal configuration links & app metadata",
                    timestamp = System.currentTimeMillis()
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
