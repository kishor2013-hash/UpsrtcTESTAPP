package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun FooterBranding(
    partnerName: String = "Grofasto Digital Solutions",
    tagline: String = "GROW | CONNECT | SUCCEED.",
    phone: String = "+91 9457690255",
    website: String = "www.grofasto.com",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .testTag("footer_branding"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(
            color = BorderDark.copy(alpha = 0.6f),
            thickness = 1.dp,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        Text(
            text = "Powered by $partnerName",
            color = TextWhite.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = tagline,
            color = CyanAccent.copy(alpha = 0.9f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mobile: $phone",
                color = TextGray,
                fontSize = 11.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                }
            )

            Text(
                text = "•",
                color = TextMuted,
                fontSize = 11.sp
            )

            Text(
                text = website,
                color = CyanAccent.copy(alpha = 0.8f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    val webUrl = if (website.startsWith("http")) website else "https://$website"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                    context.startActivity(intent)
                }
            )
        }
    }
}
