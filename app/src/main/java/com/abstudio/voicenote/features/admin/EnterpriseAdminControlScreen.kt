package com.abstudio.voicenote.features.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.sp
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import com.abstudio.voicenote.core.ui.components.SimpleLineChart
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun EnterpriseAdminControlScreen(
    onBack: () -> Unit,
    onManagePlan: () -> Unit
) {
    var showAddUserDialog by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HeaderSection()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Usage & Plan Section
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        UsageLimitCard(modifier = Modifier.weight(1f))
                        PlanOverviewCard(modifier = Modifier.weight(1f), onManagePlan = onManagePlan)
                    }
                }

                // Weekly Analytics Section
                item {
                    WeeklyAnalyticsCard()
                }

                // Authorized Zones
                item {
                    AuthorizedZonesCard()
                }

                // Team Permissions
                item {
                    TeamPermissionsCard(onAddUser = { showAddUserDialog = true })
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
            
            if (showAddUserDialog) {
                var inviteEmail by remember { mutableStateOf("") }
                val context = LocalContext.current
                AlertDialog(
                    onDismissRequest = { showAddUserDialog = false },
                    title = { Text("Add Team Member", color = Color.White) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = inviteEmail,
                                onValueChange = { inviteEmail = it },
                                label = { Text("Email Address") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryBlue,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Text("The user will receive an invite to join VoiceNote Corp.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { 
                                if (inviteEmail.isNotBlank()) {
                                    Toast.makeText(context, "Invitation sent to $inviteEmail", Toast.LENGTH_SHORT).show()
                                    showAddUserDialog = false 
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Send Invite")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddUserDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                    },
                    containerColor = Color(0xFF1A222E)
                )
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Brush.linearGradient(listOf(PrimaryBlue, Color(0xFF60A5FA))), CircleShape)
            )
            Column {
                Text("Enterprise Admin", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("VoiceNote Corp", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.Settings, "Settings", tint = Color.Gray)
        }
    }
}

@Composable
fun UsageLimitCard(modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFF1A222E),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Usage Limit", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text("Buy", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            
            Box(modifier = Modifier.size(100.dp).padding(8.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = 0.845f,
                    modifier = Modifier.fillMaxSize(),
                    color = PrimaryBlue,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.05f)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("84.5%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Used", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("8.4k / 10k", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text("Safe", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PlanOverviewCard(modifier: Modifier = Modifier, onManagePlan: () -> Unit) {
    Surface(
        color = Color(0xFF1A222E),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Text("Plan Overview", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 12.dp)) {
                PlanItem("Renewal", "Oct 24")
                PlanItem("Seats", "12 / 15")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Speed", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    Surface(color = PrimaryBlue.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("Ultra Fast", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), color = PrimaryBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Button(
                onClick = onManagePlan,
                modifier = Modifier.fillMaxWidth().height(32.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Manage", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PlanItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeeklyAnalyticsCard() {
    Surface(
        color = Color(0xFF1A222E),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Weekly Analytics", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Voice processing usage trends", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Surface(color = Color(0xFF10B981).copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                    Text("+12%", modifier = Modifier.padding(4.dp), color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            // Simple Line Chart
            SimpleLineChart(
                dataPoints = listOf(10f, 25f, 15f, 45f, 30f, 60f, 55f),
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
    }
}

@Composable
fun AuthorizedZonesCard() {
    Surface(
        color = Color(0xFF1A222E),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Authorized Zones", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Edit Zones", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            
            // Placeholder for Map
            Box(
                modifier = Modifier.fillMaxWidth().height(140.dp).background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("Interactive Map View", color = Color.Gray)
            }
        }
    }
}

@Composable
fun TeamPermissionsCard(onAddUser: () -> Unit) {
    Surface(
        color = Color(0xFF1A222E),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Team Permissions", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Button(
                    onClick = onAddUser,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            
            // Member List
            Column {
                MemberItem("Alice Chen", "Admin", PrimaryBlue)
                MemberItem("Mark D.", "Editor", Color(0xFFA855F7))
                MemberItem("Sarah J.", "Viewer", Color.Gray)
            }
            
            TextButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("View All Team Members", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun MemberItem(name: String, role: String, roleColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(36.dp).background(Color.Gray.copy(alpha = 0.2f), CircleShape))
            Column {
                Text(name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                Text(name.lowercase().replace(" ", "") + "@voicenote.com", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(color = roleColor.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                Text(role, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), color = roleColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.MoreVert, "More", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}
