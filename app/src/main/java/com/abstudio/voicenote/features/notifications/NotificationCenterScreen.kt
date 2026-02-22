package com.abstudio.voicenote.features.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abstudio.voicenote.data.models.NotificationEntity
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun NotificationCenterScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onViewInsight: (String) -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ChevronLeft, "Back", tint = Color.LightGray)
                    }
                    Text(
                        "Notifications",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                TextButton(onClick = { viewModel.markAllAsRead() }) {
                    Text("Mark all as read", color = PrimaryBlue, style = MaterialTheme.typography.labelMedium)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                val (new, earlier) = notifications.partition { !it.isRead }

                if (new.isNotEmpty()) {
                    item {
                        SectionHeader("New")
                    }
                    items(new) { notification ->
                        NotificationItem(
                            notification = notification,
                            onAction = { onViewInsight(notification.id) },
                            onDismiss = { viewModel.dismissNotification(notification) }
                        )
                    }
                }

                if (earlier.isNotEmpty()) {
                    item {
                        SectionHeader("Earlier")
                    }
                    items(earlier) { notification ->
                        NotificationItem(
                            notification = notification,
                            onAction = { onViewInsight(notification.id) },
                            onDismiss = { viewModel.dismissNotification(notification) },
                            isRead = true
                        )
                    }
                }

                if (notifications.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No notifications yet", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp, top = 16.dp),
        letterSpacing = 1.sp
    )
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onAction: () -> Unit,
    onDismiss: () -> Unit,
    isRead: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isRead) Color.Transparent else Color.White.copy(alpha = 0.03f))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Unread dot
            if (!isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(8.dp)
                        .background(PrimaryBlue, CircleShape)
                )
            } else {
                Spacer(modifier = Modifier.size(8.dp))
            }

            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        when (notification.type) {
                            "AI_INSIGHT" -> PrimaryBlue.copy(alpha = 0.1f)
                            "WORKSPACE_INVITE" -> Color(0xFF6200EE).copy(alpha = 0.1f)
                            "DEADLINE" -> Color.Red.copy(alpha = 0.1f)
                            else -> Color.Gray.copy(alpha = 0.1f)
                        },
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (notification.type) {
                        "AI_INSIGHT" -> "✨"
                        "WORKSPACE_INVITE" -> "🏢"
                        "DEADLINE" -> "⏰"
                        else -> "🔔"
                    },
                    fontSize = 20.sp
                )
            }

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "2m ago", // Placeholder for actual time conversion
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Text(
                    notification.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )

                if (!isRead) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onAction,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(notification.actionLabel ?: "View", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onDismiss,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Dismiss", fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
