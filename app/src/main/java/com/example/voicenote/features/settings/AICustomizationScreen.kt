package com.example.voicenote.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
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
fun AICustomizationScreen(
    onBack: () -> Unit,
    onApply: (AIRole, Float, CommStyle) -> Unit
) {
    var selectedRole by remember { mutableStateOf(AIRole.DEVELOPER) }
    var temperature by remember { mutableFloatStateOf(0.7f) }
    var selectedStyle by remember { mutableStateOf(CommStyle.DETAILED) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ChevronLeft, "Back", tint = Color.LightGray)
                }
                Text("AI Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                TextButton(onClick = {}) {
                    Text("Reset", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }

            // Hero
            Text("Customize your AI", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                "Choose a persona and behavior pattern to tailor VoiceNote insights to your workflow.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // AI Role
            SectionHeaderWithAction("AI Role", "Compare Roles")
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(240.dp)
            ) {
                items(AIRole.entries) { role ->
                    RoleCard(
                        role = role,
                        isSelected = selectedRole == role,
                        onClick = { selectedRole = role }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Behavior & Tone
            SectionHeaderWithAction("Behavior & Tone", null)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(24.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                // Temperature
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Output Temperature", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("%.1f".format(temperature), style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
                }
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    colors = SliderDefaults.colors(thumbColor = PrimaryBlue, activeTrackColor = PrimaryBlue)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("STRICT", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("BALANCED", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Text("CREATIVE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Communication Style
                Text("Communication Style", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    CommStyle.entries.forEach { style ->
                        val selected = style == selectedStyle
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedStyle = style },
                            color = if (selected) Color.White.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = style.name.lowercase().capitalize(),
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                color = if (selected) PrimaryBlue else Color.Gray,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.width(4.dp).height(48.dp).background(PrimaryBlue, RoundedCornerShape(2.dp)))
                    Text(
                        "\"Based on the discussion regarding the API refactor, we need to initialize the UserAuth module by Q3...\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        modifier = Modifier.padding(trailing = 48.dp)
                    )
                }
            }

            // Footer Button
            Button(
                onClick = { onApply(selectedRole, temperature, selectedStyle) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Apply Changes", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SectionHeaderWithAction(title: String, action: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
        if (action != null) {
            Text(action, style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RoleCard(role: AIRole, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSelected) PrimaryBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.03f), RoundedCornerShape(16.dp))
            .border(1.dp, if (isSelected) PrimaryBlue else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.size(32.dp).background(if (isSelected) PrimaryBlue else Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(role.icon, fontSize = 16.sp)
            }
            Column {
                Text(role.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(role.desc, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 9.sp, lineHeight = 12.sp)
            }
        }
        if (isSelected) {
            Text("✅", modifier = Modifier.align(Alignment.TopEnd), fontSize = 14.sp)
        }
    }
}

enum class AIRole(val title: String, val icon: String, val desc: String) {
    DEVELOPER("Developer", "💻", "Optimized for code snippets & tech specs."),
    MANAGER("Manager", "💼", "Focus on action items & strategy."),
    STUDENT("Student", "🎓", "Detailed summaries & definitions."),
    EXECUTIVE("Executive", "📊", "High-level bullet points only.")
}

enum class CommStyle { CONCISE, DETAILED, BULLETED }
