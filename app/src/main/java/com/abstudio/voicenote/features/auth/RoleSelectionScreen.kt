package com.abstudio.voicenote.features.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abstudio.voicenote.core.ui.components.GlassCard
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

data class RoleItem(val title: String, val icon: String, val role: String)

val ROLES = listOf(
    RoleItem("Individual", "👤", "INDIVIDUAL"),
    RoleItem("Team Member", "👥", "TEAM"),
    RoleItem("Developer", "👨‍💻", "DEVELOPER"),
    RoleItem("Executive", "💼", "EXECUTIVE"),
    RoleItem("Healthcare", "🩺", "HEALTHCARE"),
    RoleItem("Legal", "⚖️", "LEGAL")
)

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Text(
            text = "Select Your Path",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Are you using VoiceNote for personal insights or team collaboration?",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(ROLES) { item ->
                val isSelected = selectedRole == item.role
                GlassCard(
                    modifier = Modifier
                        .height(140.dp)
                        .clickable { selectedRole = item.role },
                    cornerRadius = 20.dp,
                    borderColor = if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.1f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(item.icon, fontSize = 40.sp)
                        Text(
                            item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) PrimaryBlue else Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        Button(
            onClick = { if (selectedRole.isNotEmpty()) onRoleSelected(selectedRole) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            enabled = selectedRole.isNotEmpty()
        ) {
            Text("Complete Setup", style = MaterialTheme.typography.titleLarge)
        }
    }
}
