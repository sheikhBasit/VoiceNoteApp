package com.abstudio.voicenote.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule

data class HeroTask(
    val title: String,
    val time: String,
    val location: String,
    val priority: String, // HIGH, MEDIUM, LOW
    val attendeeAvatars: List<String>,
    val backgroundImage: String? = null
)

@Composable
fun HeroTaskCard(
    task: HeroTask,
    onJoinClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Background Image
        if (task.backgroundImage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
            )
        }
        
        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Black.copy(alpha = 0.6f),
                            Color.Black.copy(alpha = 0.9f)
                        )
                    )
                )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Priority Badge + Attendees
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Priority Badge
                val (badgeColor, badgeText) = when (task.priority) {
                    "HIGH" -> Color(0xFFEF4444).copy(alpha = 0.2f) to "High Priority"
                    "MEDIUM" -> Color(0xFFF59E0B).copy(alpha = 0.2f) to "Medium Priority"
                    else -> Color(0xFF10B981).copy(alpha = 0.2f) to "Low Priority"
                }
                
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (task.priority) {
                            "HIGH" -> Color(0xFFEF4444).copy(alpha = 0.3f)
                            "MEDIUM" -> Color(0xFFF59E0B).copy(alpha = 0.3f)
                            else -> Color(0xFF10B981).copy(alpha = 0.3f)
                        }
                    )
                ) {
                    Text(
                        text = badgeText.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (task.priority) {
                            "HIGH" -> Color(0xFFFECDD3)
                            "MEDIUM" -> Color(0xFFFED7AA)
                            else -> Color(0xFFBBF7D0)
                        }
                    )
                }
                
                // Attendee Avatars
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    task.attendeeAvatars.take(3).forEach { avatar ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }
                    if (task.attendeeAvatars.size > 3) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${task.attendeeAvatars.size - 3}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            
            // Bottom: Task Info
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = task.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = task.time,
                        fontSize = 14.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF94A3B8))
                    )
                    Text(
                        text = task.location,
                        fontSize = 14.sp,
                        color = Color(0xFFCBD5E1)
                    )
                }
                
                Button(
                    onClick = onJoinClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.abstudio.voicenote.core.ui.theme.PrimaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "View Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
