package com.example.voicenote.features.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.components.GlassCard
import com.example.voicenote.ui.theme.PrimaryBlue

@Composable
fun PermissionsGuideScreen(
    onGrantAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Access Permissions",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 16.dp)
        )

        Text(
            text = "VoiceNote AI needs access to several system features to provide its full intelligence suite.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        listOf(
            "🎙️" to "Microphone Access (for high-fidelity recording)",
            "📅" to "Calendar Access (to sync with your meetings)",
            "📍" to "Location Access (for context-aware notes)",
            "📇" to "Contacts Access (to assign tasks to teammates)"
        ).forEach { (icon, desc) ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                cornerRadius = 16.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(desc, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onGrantAll,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Grant & Continue", style = MaterialTheme.typography.titleLarge)
        }
    }
}
