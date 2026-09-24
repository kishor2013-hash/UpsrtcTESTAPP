package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite

@Composable
fun PortalCard(
    title: String,
    badgeText: String,
    description: String,
    buttonText: String,
    icon: ImageVector,
    themeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B172E),
                        Color(0xFF070F20)
                    )
                )
            )
            .border(
                1.3.dp,
                Brush.linearGradient(
                    colors = listOf(
                        themeColor.copy(alpha = 0.85f),
                        themeColor.copy(alpha = 0.25f)
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top Row: Icon + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.radialGradient(
                                colors = listOf(themeColor.copy(alpha = 0.22f), Color(0xFF0A1528))
                            )
                        )
                        .border(1.dp, themeColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = themeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Badge Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF091730))
                        .border(1.dp, themeColor.copy(alpha = 0.7f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = themeColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Card Title
            Text(
                text = title,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = description,
                color = TextGray,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 14.5.sp,
                minLines = 3,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pill Action Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(themeColor.copy(alpha = 0.08f))
                    .border(1.dp, themeColor, RoundedCornerShape(50.dp))
                    .clickable { onClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = buttonText,
                    color = themeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.2.sp,
                    maxLines = 1
                )
            }
        }
    }
}
