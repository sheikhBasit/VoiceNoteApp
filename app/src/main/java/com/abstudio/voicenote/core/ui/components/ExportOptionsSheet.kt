package com.abstudio.voicenote.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun ExportOptionsSheet(
    onDismiss: () -> Unit,
    onExport: (ExportFormat) -> Unit,
    exportState: ExportState = ExportState.Idle
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        // Drag Handle
        Box(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .width(40.dp)
                .height(4.dp)
                .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
                .align(Alignment.CenterHorizontally)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Export Options", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, "Close", tint = Color.Gray)
            }
        }

        // Success State
        if (exportState is ExportState.Success) {
            Box(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(color = PrimaryBlue, shape = CircleShape, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.CheckCircle, "Success", tint = Color.White, modifier = Modifier.padding(4.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Your file is ready", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("All data processed successfully", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Button(
                        onClick = { /* Handle Download */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Download", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Action List
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            ExportFormat.entries.forEach { format ->
                ExportOptionItem(
                    format = format,
                    onClick = { onExport(format) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            Text("Cancel", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExportOptionItem(format: ExportFormat, onClick: () -> Unit) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(format.color.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(format.icon, fontSize = 24.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(format.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(format.subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, "Go", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

enum class ExportFormat(val title: String, val subtitle: String, val icon: String, val color: Color) {
    PDF("Export as PDF", "Standard document format", "📄", Color.Red),
    MARKDOWN("Export as Markdown", "Lightweight plain text", "📝", PrimaryBlue),
    NOTION("Export to Notion", "Sync to workspace", "📓", Color.White),
    SLACK("Export to Slack", "Share with team", "💬", Color(0xFF9333EA))
}

sealed class ExportState {
    object Idle : ExportState()
    object Processing : ExportState()
    object Success : ExportState()
    data class Error(val message: String) : ExportState()
}
