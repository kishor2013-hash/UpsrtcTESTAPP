package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.YellowAccent
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UpsrtcHeader(
    appName: String = "UPSRTC ROADWAYS",
    appVersion: String = "DIGITAL DUTY PORTAL V4.0",
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now).uppercase()
            delay(1000)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("upsrtc_header"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Glowing Bus Logo Box and App Titles matching reference image
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(CyanAccent.copy(alpha = 0.45f), Color(0xFF072138))
                        )
                    )
                    .border(1.5.dp, CyanAccent, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "UPSRTC Bus Logo",
                    tint = CyanAccent,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                // Top line: UPSRTC ROADWAYS in bold Golden Yellow
                Text(
                    text = appName,
                    color = YellowAccent,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp
                )
                // Bottom line: DIGITAL DUTY PORTAL V4.0 in bright Cyan
                Text(
                    text = appVersion,
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Right: Live Digital Clock matching reference image
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = currentTime.ifBlank { "09:17:15" },
                color = YellowAccent,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
            Text(
                text = currentDate.ifBlank { "22 SEPT 2026" },
                color = Color(0xFFA5C4DC),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )
        }
    }
}
