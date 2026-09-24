package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.DutyRecord
import com.example.data.model.User
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateAndShareDutyReport(
        context: Context,
        user: User,
        monthName: String,
        records: List<DutyRecord>,
        totalDuties: Int,
        totalKm: Double,
        totalIncome: Double,
        avgKm: Double,
        avgIncome: Double,
        avgLoadFactor: Double
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (in points)
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(10, 30, 60)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.rgb(0, 150, 180)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.rgb(40, 40, 40)
            textSize = 10f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = Color.rgb(20, 20, 20)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(200, 210, 220)
            strokeWidth = 1f
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(235, 245, 255)
        }

        val timeFormat = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.ENGLISH)
        val currentTime = timeFormat.format(Date())

        var y = 40f

        // Document Header
        canvas.drawText("UPSRCTC ROADWAYS", 40f, y, titlePaint)
        y += 18f
        canvas.drawText("DIGITAL DUTY PORTAL V4.0 — MONTHLY DUTY REPORT", 40f, y, subtitlePaint)
        y += 24f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 16f

        // Employee Info Box
        canvas.drawRect(40f, y - 5f, 555f, y + 55f, headerBgPaint)
        canvas.drawText("Employee Name: ${user.fullName}", 50f, y + 12f, boldTextPaint)
        canvas.drawText("Employee ID / CND: ${user.employeeId}", 320f, y + 12f, boldTextPaint)
        canvas.drawText("Role: ${user.employeeType.name} (${user.designation})", 50f, y + 30f, textPaint)
        canvas.drawText("Depot: ${user.depot}", 320f, y + 30f, textPaint)
        canvas.drawText("Reporting Period: $monthName", 50f, y + 48f, textPaint)
        canvas.drawText("Generated On: $currentTime", 320f, y + 48f, textPaint)

        y += 75f

        // Table Header
        canvas.drawRect(40f, y - 10f, 555f, y + 12f, Paint().apply { color = Color.rgb(220, 230, 240) })
        canvas.drawText("Date", 45f, y + 4f, boldTextPaint)
        canvas.drawText("Duty #", 110f, y + 4f, boldTextPaint)
        canvas.drawText("Bus Number", 165f, y + 4f, boldTextPaint)
        canvas.drawText("Route", 260f, y + 4f, boldTextPaint)
        canvas.drawText("KM", 410f, y + 4f, boldTextPaint)
        canvas.drawText("Income (₹)", 460f, y + 4f, boldTextPaint)
        canvas.drawText("LF %", 525f, y + 4f, boldTextPaint)

        y += 20f

        // Duty rows (up to 22 entries on page 1)
        val displayRecords = records.take(22)
        for (rec in displayRecords) {
            canvas.drawLine(40f, y - 4f, 555f, y - 4f, linePaint)
            canvas.drawText(rec.dutyDate, 45f, y + 8f, textPaint)
            canvas.drawText(rec.dutyNumber, 110f, y + 8f, textPaint)
            canvas.drawText(rec.busNumber, 165f, y + 8f, textPaint)
            val shortRoute = if (rec.route.length > 22) rec.route.take(20) + ".." else rec.route
            canvas.drawText(shortRoute, 260f, y + 8f, textPaint)
            canvas.drawText("%.1f".format(rec.totalKm), 410f, y + 8f, textPaint)
            canvas.drawText("₹%.0f".format(rec.income), 460f, y + 8f, boldTextPaint)
            canvas.drawText("%.1f%%".format(rec.loadFactor), 525f, y + 8f, textPaint)
            y += 18f
        }

        if (displayRecords.isEmpty()) {
            canvas.drawText("No duty records found for the selected month.", 180f, y + 20f, textPaint)
            y += 40f
        }

        y += 15f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 20f

        // Monthly Summary & Statistics
        canvas.drawText("MONTHLY PERFORMANCE SUMMARY", 40f, y, boldTextPaint)
        y += 18f

        canvas.drawRect(40f, y - 5f, 555f, y + 45f, headerBgPaint)
        canvas.drawText("Total Duties: $totalDuties", 50f, y + 14f, boldTextPaint)
        canvas.drawText("Total Distance: %.1f KM".format(totalKm), 210f, y + 14f, boldTextPaint)
        canvas.drawText("Total Income: ₹%.2f".format(totalIncome), 380f, y + 14f, boldTextPaint)

        canvas.drawText("Average KM/Duty: %.1f KM".format(avgKm), 50f, y + 34f, textPaint)
        canvas.drawText("Average Income/Duty: ₹%.2f".format(avgIncome), 210f, y + 34f, textPaint)
        canvas.drawText("Average Load Factor: %.1f%%".format(avgLoadFactor), 380f, y + 34f, textPaint)

        y += 65f

        // Verification & Footer
        canvas.drawText("Official Verification & System Stamp:", 40f, y, boldTextPaint)
        y += 14f
        canvas.drawText("Digitally verified record from UPSRTC Roadways Control Deck.", 40f, y, textPaint)
        canvas.drawText("Status: OFFICIAL VERIFIED DOCUMENT", 340f, y, subtitlePaint)
        y += 28f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 16f
        val footerPaint = Paint().apply {
            color = Color.GRAY
            textSize = 8f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }
        canvas.drawText("Powered by Grofasto Digital Solutions | GROW • CONNECT • SUCCEED | +91 9457690255", 100f, y, footerPaint)

        pdfDocument.finishPage(page)

        // Write to cache file
        try {
            val cachePath = File(context.cacheDir, "reports")
            cachePath.mkdirs()
            val file = File(cachePath, "UPSRTC_Report_${user.employeeId}_$monthName.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Share / view Intent
            val contentUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "UPSRTC Duty Report - ${user.employeeId} ($monthName)")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share or View Duty PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            pdfDocument.close()
        }
    }
}
