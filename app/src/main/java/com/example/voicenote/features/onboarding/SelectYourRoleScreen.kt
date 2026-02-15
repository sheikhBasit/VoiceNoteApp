package com.example.voicenote.features.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.theme.PrimaryBlue

@Composable
fun SelectYourRoleScreen(
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onContinue: (SetupRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf<SetupRole?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        StepIndicator(isActive = true)
                        StepIndicator(isActive = false)
                        StepIndicator(isActive = false)
                    }

                    TextButton(onClick = onSkip) {
                        Text("Skip", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }

                // Content
                Column(modifier = Modifier.padding(vertical = 24.dp)) {
                    Text("What’s your focus?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        "We'll customize your AI insights and note summaries based on your daily workflow.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SetupRole.entries) { role ->
                        RoleSelectionItem(
                            role = role,
                            isSelected = selectedRole == role,
                            onClick = { selectedRole = role }
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(120.dp)) }
                }
            }

            // Bottom CTA
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.9f), MaterialTheme.colorScheme.background)
                        )
                    )
                    .padding(24.dp)
                    .padding(bottom = 16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { selectedRole?.let { onContinue(it) } },
                        enabled = selectedRole != null,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Text("Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, "Go")
                    }
                    Text(
                        "You can change this anytime in settings",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StepIndicator(isActive: Boolean) {
    Box(
        modifier = Modifier
            .width(24.dp)
            .height(6.dp)
            .background(if (isActive) PrimaryBlue else Color.White.copy(alpha = 0.1f), CircleShape)
    )
}

@Composable
fun RoleSelectionItem(role: SetupRole, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp, 
            if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.05f)
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (isSelected) PrimaryBlue else PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    role.icon, 
                    "Icon", 
                    tint = if (isSelected) Color.White else PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(role.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(role.desc, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, "Selected", tint = PrimaryBlue)
            }
        }
    }
}

enum class SetupRole(val title: String, val desc: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MANAGER("Manager", "Focus on action items, team alignment, and performance tracking.", Icons.Default.Groups),
    DEVELOPER("Developer", "Tailored for technical specs, documentation, and bug reporting.", Icons.Default.Terminal),
    SALES("Sales", "Identify client needs, follow-up tasks, and CRM automation.", Icons.Default.Handshake),
    EXECUTIVE("Executive", "High-level strategic summaries and trend identification.", Icons.Default.Insights)
}
