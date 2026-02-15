package com.example.voicenote.features.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.theme.PrimaryBlue

@Composable
fun UserProfileStatsScreen(
    onBack: () -> Unit,
    onSettingsClick: () -> Unit,
    onSignOut: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIos, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text("Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, "Settings", tint = Color.White)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 24.dp)) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .border(4.dp, PrimaryBlue.copy(alpha = 0.2f), CircleShape)
                                    .padding(4.dp)
                                    .background(Color.Gray, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(PrimaryBlue, CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✎", color = Color.White, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("John Doe", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Senior Solutions Architect", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                        Text("john.doe@enterprise.com", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }

                // Quick Actions
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                        ) {
                            Text("Edit Profile", color = Color.White)
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Security", color = Color.White)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Stats Grid
                item {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("Usage Statistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Total Notes", "128", "+12%", Modifier.weight(1f))
                            StatCard("AI Accuracy", "98%", "Stable", Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard("Time Saved", "42.5h", "+5h", Modifier.weight(1f))
                            StatCard("Collab Index", "8.5", "+0.4", Modifier.weight(1f))
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // AI Insights Card
                item {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("AI Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(listOf(PrimaryBlue.copy(alpha = 0.15f), PrimaryBlue.copy(alpha = 0.05f))),
                                    RoundedCornerShape(16.dp)
                                )
                                .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(PrimaryBlue, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("✨", fontSize = 20.sp)
                                    }
                                    Column {
                                        Text("Productivity Trend", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("EFFICIENCY ANALYSIS", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                                Text(
                                    "Your meetings are 15% shorter since you started using VoiceNote's auto-summarization feature. You're saving an average of 12 minutes per sync!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray,
                                    lineHeight = 18.sp
                                )
                                LinearProgressIndicator(
                                    progress = 0.75f,
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                    color = PrimaryBlue,
                                    trackColor = Color.White.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Administration Table
                item {
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("Administration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                        ) {
                            AdminRow("Enterprise Plan", "PRO") {}
                            Divider(color = Color.White.copy(alpha = 0.1f))
                            AdminRow("Notification Preferences", null) {}
                            Divider(color = Color.White.copy(alpha = 0.1f))
                            AdminRow("Sign Out", null, isDestructive = true, onClick = onSignOut)
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("VoiceNote Enterprise v4.2.0", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("Last synced: Today, 10:42 AM", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, change: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            Text(change, style = MaterialTheme.typography.labelSmall, color = Color(0xFF0BDA5E), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminRow(label: String, badge: String? = null, isDestructive: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (isDestructive) "🚪" else "⚙️",
                fontSize = 18.sp
            )
            Text(
                text = label,
                color = if (isDestructive) Color.Red else Color.White,
                fontWeight = FontWeight.Medium
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (badge != null) {
                Surface(color = PrimaryBlue, shape = RoundedCornerShape(4.dp)) {
                    Text(badge, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("›", color = Color.Gray, fontSize = 20.sp)
        }
    }
}
