package com.abstudio.voicenote.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abstudio.voicenote.core.ui.components.GlassCard
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description

import androidx.compose.material.icons.filled.Info

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToNote: (String) -> Unit,
    onNavigateToVault: () -> Unit,
    onNavigateToTaskCenter: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAura: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator(color = PrimaryBlue)
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToAura) {
                        Icon(androidx.compose.material.icons.Icons.Default.AutoAwesome, "AI Assistant", tint = PrimaryBlue)
                    }
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(androidx.compose.material.icons.Icons.Default.Notifications, "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Remove the manual "Dashboard" text item since it's in the TopAppBar
            
            // Hero Task Card - Up Next
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Up Next", style = MaterialTheme.typography.titleLarge, color = Color.White)
                TextButton(onClick = onNavigateToTaskCenter) {
                    Text("See all", color = PrimaryBlue)
                }
            }
        }
        
        item {
            if (state.isRecording) {
                 GlassCard(
                     modifier = Modifier.fillMaxWidth().height(200.dp)
                 ) {
                     Column(
                         modifier = Modifier.fillMaxSize().padding(20.dp),
                         verticalArrangement = Arrangement.Center,
                         horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                     ) {
                         Icon(
                             androidx.compose.material.icons.Icons.Default.Mic,
                             contentDescription = null,
                             tint = Color.Red,
                             modifier = Modifier.size(48.dp)
                         )
                         Spacer(modifier = Modifier.height(16.dp))
                         Text("Recording in Progress...", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                         Text("Listening and syncing in real-time", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                     }
                 }
            } else {
                com.abstudio.voicenote.core.ui.components.HeroTaskCard(
                    task = com.abstudio.voicenote.core.ui.components.HeroTask(
                        title = "Next Strategy Session",
                        time = "14:00 PM - 15:00 PM",
                        location = "HQ Meeting Room",
                        priority = "MEDIUM",
                        attendeeAvatars = listOf(
                            "https://i.pravatar.cc/150?img=1",
                            "https://i.pravatar.cc/150?img=2"
                        ),
                        backgroundImage = null
                    ),
                    onJoinClick = {
                        // View Task details
                    }
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Productivity Velocity", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    Text(
                        state.stats?.velocity ?: "--",
                        style = MaterialTheme.typography.displayMedium,
                        color = PrimaryBlue
                    )
                }
            }
        }

        
        if (state.aiInsights.isNotEmpty()) {
            item {
               Text("AI Insights", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            items(state.aiInsights) { insight ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                           androidx.compose.material.icons.Icons.Default.Info.let { icon ->
                               Icon(icon, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(20.dp))
                           }
                           Spacer(modifier = Modifier.width(8.dp))
                           Text(insight.title, style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(insight.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Tasks Due Today", style = MaterialTheme.typography.titleMedium, color = Color.White)
                TextButton(onClick = onNavigateToTaskCenter) {
                    Text("View All", color = PrimaryBlue)
                }
            }
        }

        items(state.tasksDueToday) { task ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(task.description, color = Color.White)
                    Text(task.priority, color = if(task.priority == "HIGH") Color.Red else Color.Gray)
                }
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Recent Notes", style = MaterialTheme.typography.titleMedium, color = Color.White)
                TextButton(onClick = onNavigateToVault) {
                    Text("View All", color = PrimaryBlue)
                }
            }
        }
        
        items(state.recentNotes) { note ->
             Card(
                onClick = { onNavigateToNote(note.id) },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    Text(note.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Text(note.status, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
        }
    }
}
