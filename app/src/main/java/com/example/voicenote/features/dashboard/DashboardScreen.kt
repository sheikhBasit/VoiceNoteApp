package com.example.voicenote.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.data.models.DashboardResponse
import com.example.voicenote.ui.components.GlassCard
import com.example.voicenote.ui.components.GradientCard
import com.example.voicenote.ui.theme.PrimaryBlue
import com.example.voicenote.ui.theme.PrimaryBlueVariant
import com.example.voicenote.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { DashboardBottomNavigation() },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { /* Navigate to Voice Capture */ },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.offset(y = 40.dp) // Align with bottom bar partially as per mock
            ) {
                Text("🎙️", fontSize = 32.sp)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is DashboardState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardState.Success -> {
                    DashboardContent(state.data)
                }
                is DashboardState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(data: DashboardResponse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DashboardHeader()
        }
        
        item {
            UpNextCard(data.stats.totalTasks)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(
                    title = "Meeting ROI",
                    value = data.stats.meetingRoi ?: "0%",
                    modifier = Modifier.weight(1f),
                    color = PrimaryBlue
                )
                MetricCard(
                    title = "Velocity",
                    value = data.stats.velocity ?: "0%",
                    modifier = Modifier.weight(1f),
                    color = PrimaryBlueVariant
                )
            }
        }

        item {
            Text(
                "AI Insights",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(data.aiInsights) { insight ->
            InsightCard(insight.title, insight.description)
        }

        item {
            Text(
                "Recent Recordings",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        items(data.recentNotes) { note ->
            NoteCard(note.title, note.status)
        }
        
        // Add some space at the bottom for the FAB if we add one later
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Intelligence Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Monday, 15 Feb",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.1f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("👤", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun UpNextCard(taskCount: Int) {
    GradientCard(
        modifier = Modifier.fillMaxWidth(),
        colors = listOf(PrimaryBlue, PrimaryBlueVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("UP NEXT", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
            Text("Refinement Meeting", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Text("10:30 AM - 11:15 AM", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("$taskCount Tasks pending", style = MaterialTheme.typography.bodySmall, color = Color.White)
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier, color: Color) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InsightCard(title: String, description: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("💡", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}

@Composable
fun NoteCard(title: String, status: String) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text("2 mins ago", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(
                status,
                style = MaterialTheme.typography.labelSmall,
                color = if (status == "PROCESSED") PrimaryBlueVariant else PrimaryBlue,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.05f), MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun DashboardBottomNavigation() {
    NavigationBar(
        containerColor = Color.Transparent, // We'll use a glass effect background
        contentColor = Color.White,
        modifier = Modifier.background(Color.Black.copy(alpha = 0.5f))
    ) {
        NavigationBarItem(
            icon = { Text("🏠", fontSize = 24.sp) },
            label = { Text("Home") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Text("📝", fontSize = 24.sp) },
            label = { Text("Notes") },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Text("✅", fontSize = 24.sp) },
            label = { Text("Tasks") },
            selected = false,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Text("⚙️", fontSize = 24.sp) },
            label = { Text("Settings") },
            selected = false,
            onClick = {}
        )
    }
}
