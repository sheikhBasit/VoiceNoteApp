package com.abstudio.voicenote.features.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abstudio.voicenote.core.analytics.AnalyticsTracker
import com.abstudio.voicenote.core.ui.components.GlassCard
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import com.abstudio.voicenote.core.ui.theme.TextSecondary
import com.abstudio.voicenote.data.local.entities.NoteEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String?,
    onBack: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Transcript", "Insights", "Tasks", "AI Chat")

    LaunchedEffect(noteId) {
        if (noteId != null) {
            viewModel.loadNoteDetail(noteId)
            AnalyticsTracker.trackScreenView("note_detail")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.note?.title ?: "Note Detail", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("\u2190", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PrimaryBlue
                )
            } else if (uiState.error != null) {
                uiState.error?.let { errorMessage ->
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                uiState.note?.let { note ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Tabs
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = Color.White,
                            edgePadding = 0.dp
                        ) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = { Text(title) }
                                )
                            }
                        }

                        // Content
                        when (selectedTab) {
                            0, 1, 2 -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    when (selectedTab) {
                                        0 -> TranscriptView(note)
                                        1 -> InsightsView(note)
                                        2 -> TasksView(uiState.tasks, onToggleTask = { id, done -> viewModel.toggleTask(id, done) })
                                    }
                                }
                            }
                            3 -> NoteChatView(
                                noteId = noteId ?: "",
                                messages = uiState.chatMessages,
                                isLoading = uiState.isChatLoading,
                                onSend = { question -> viewModel.askNoteQuestion(noteId ?: "", question) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteChatView(
    noteId: String,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onSend: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Chat messages
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Text(
                        "Ask anything about this note...",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
            items(messages) { message ->
                ChatBubble(message)
            }
            if (isLoading) {
                item {
                    Row(modifier = Modifier.padding(start = 4.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = PrimaryBlue,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thinking...", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Input field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about this note...", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                    cursorColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank() && !isLoading) {
                        onSend(inputText.trim())
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isLoading
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank() && !isLoading) PrimaryBlue else Color.Gray
                )
            }
        }
    }
}

@Composable
fun TranscriptView(note: NoteEntity) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Summary", style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
            Text(note.summary, color = Color.White, modifier = Modifier.padding(vertical = 8.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 16.dp))

            Text("Transcript", style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
            Text(note.transcript, color = TextSecondary, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun InsightsView(note: NoteEntity) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        note.semanticAnalysis?.let { analysis ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sentiment: ${analysis.sentiment}", color = Color.White)
                    Text("Tone: ${analysis.emotionalTone}", color = Color.White)
                }
            }

            if (analysis.keyInsights.isNotEmpty()) {
                Text("Key Insights", style = MaterialTheme.typography.titleMedium, color = Color.White)
                analysis.keyInsights.forEach { insight ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(insight, modifier = Modifier.padding(12.dp), color = Color.White)
                    }
                }
            }
        }

        note.businessLeads?.let { leads ->
             if (leads.isNotEmpty()) {
                 Text("Business Leads", style = MaterialTheme.typography.titleMedium, color = Color.White)
                 leads.forEach { lead ->
                     GlassCard(modifier = Modifier.fillMaxWidth()) {
                         Column(modifier = Modifier.padding(12.dp)) {
                             Text(lead.name, style = MaterialTheme.typography.titleSmall, color = PrimaryBlue)
                             Text(lead.prospectType, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                         }
                     }
                 }
             }
        }
    }
}

@Composable
fun TasksView(tasks: List<com.abstudio.voicenote.data.local.entities.TaskEntity>, onToggleTask: (String, Boolean) -> Unit) {
    if (tasks.isEmpty()) {
        Text("No tasks assigned to this note.", color = TextSecondary)
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tasks.forEach { task ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { onToggleTask(task.id, it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                task.title ?: task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                textDecoration = if (task.isDone) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                            Text(
                                "Priority: ${task.priority}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (task.priority == "CRITICAL") Color.Red else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}
