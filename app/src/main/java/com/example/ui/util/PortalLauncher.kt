package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object PortalLauncher {
    fun launchUrl(context: Context, url: String) {
        val cleanUrl = url.trim()
        if (cleanUrl.isBlank()) {
            Toast.makeText(context, "URL is not configured", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val parsedUri = Uri.parse(cleanUrl)
            val intent = Intent(Intent.ACTION_VIEW, parsedUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open external portal: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
