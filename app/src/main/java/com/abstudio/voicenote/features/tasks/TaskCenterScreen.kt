package com.abstudio.voicenote.features.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import com.abstudio.voicenote.core.ui.theme.TextSecondary
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.features.tasks.components.SuggestedActionsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCenterScreen(
    onBack: () -> Unit,
    onNavigateToAiChat: ((String) -> Unit)? = null,
    viewModel: TaskCenterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Center", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            // Summary Cards
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TaskSummaryCard("Due Today", uiState.tasksDueToday, Modifier.weight(1f), Color(0xFFFFA726))
                TaskSummaryCard("Overdue", uiState.tasksOverdue, Modifier.weight(1f), Color(0xFFEF5350))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("All Tasks", style = MaterialTheme.typography.titleMedium, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading && uiState.tasks.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = PrimaryBlue)
            } else if (uiState.tasks.isEmpty()) {
                Text("No tasks found.", color = TextSecondary, modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { isChecked -> viewModel.toggleTask(task.id, isChecked) },
                            onAiPromptClick = { prompt -> onNavigateToAiChat?.invoke(prompt) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSummaryCard(title: String, count: Int, modifier: Modifier = Modifier, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(count.toString(), style = MaterialTheme.typography.headlineLarge, color = color, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    onToggle: (Boolean) -> Unit,
    onAiPromptClick: ((String) -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = onToggle,
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryBlue,
                        uncheckedColor = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title ?: "Untitled Task",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isDone) Color.Gray else Color.White,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    if (task.description != null) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = if (expanded) Int.MAX_VALUE else 1
                        )
                    }
                }
                // Priority badge
                if (task.priority == "HIGH") {
                    Surface(
                        color = Color(0xFFEF5350).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "HIGH",
                            color = Color(0xFFEF5350),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Suggested Actions - show when expanded and actions exist
            AnimatedVisibility(visible = expanded && task.suggestedActions != null) {
                task.suggestedActions?.let { actions ->
                    SuggestedActionsRow(
                        suggestedActions = actions,
                        onAiPromptClick = onAiPromptClick
                    )
                }
            }
        }
    }
}
