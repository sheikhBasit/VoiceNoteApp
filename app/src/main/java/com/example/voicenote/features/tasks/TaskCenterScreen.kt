package com.example.voicenote.features.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.voicenote.data.models.TaskCenterResponse
import com.example.voicenote.data.models.TaskResponse
import com.example.voicenote.ui.components.GlassCard
import com.example.voicenote.ui.theme.*

@Composable
fun TaskCenterScreen(
    viewModel: TasksViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is TaskCenterState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is TaskCenterState.Success -> {
                    TaskCenterContent(state.data)
                }
                is TaskCenterState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = ErrorRed)
                }
            }
        }
    }
}

@Composable
fun TaskCenterContent(data: TaskCenterResponse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                "Task Center",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. Attention Needed (Critical)
        if (data.criticalTasks.isNotEmpty()) {
            item {
                Text("Attention Needed", style = MaterialTheme.typography.titleLarge, color = ErrorRed)
            }
            items(data.criticalTasks) { task ->
                CriticalTaskCard(task)
            }
        }

        // 2. AI Priority Queue
        item {
            Text("AI Priority Queue", style = MaterialTheme.typography.titleLarge, color = Color.White)
        }

        items(data.priorityQueue) { task ->
            PriorityTaskCard(task)
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun CriticalTaskCard(task: TaskResponse) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp
    ) {
        Column(
            modifier = Modifier
                .background(ErrorRed.copy(alpha = 0.05f))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(task.title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Text(
                task.description ?: "Critical action required immediately.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Row(modifier = Modifier.padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { /* Action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Resolve Now")
                }
            }
        }
    }
}

@Composable
fun PriorityTaskCard(task: TaskResponse) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(task.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                Text(
                    task.dueDate ?: "TBD",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryBlueVariant
                )
            }
            
            Text(
                task.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            if (task.suggestedAction != null) {
                SuggestedActionRow(task.suggestedAction)
            }

            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Dismiss */ },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text("Dismiss", color = Color.White)
                }
                Button(
                    onClick = { /* Approve */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Approve")
                }
            }
        }
    }
}

@Composable
fun SuggestedActionRow(action: com.example.voicenote.data.models.SuggestedAction) {
    Surface(
        modifier = Modifier.padding(top = 12.dp),
        color = Color.White.copy(alpha = 0.05f),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                when (action.type) {
                    "WHATSAPP" -> "💬"
                    "CALENDAR" -> "📅"
                    "MAPS" -> "📍"
                    else -> "⚡"
                },
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(action.text, style = MaterialTheme.typography.labelMedium, color = PrimaryBlueVariant)
        }
    }
}
import androidx.compose.foundation.BorderStroke
