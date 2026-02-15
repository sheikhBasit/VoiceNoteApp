package com.example.voicenote.features.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.voicenote.data.models.Conflict
import com.example.voicenote.data.models.NoteDetailResponse
import com.example.voicenote.data.models.TranscriptSegment
import com.example.voicenote.ui.components.GlassCard
import com.example.voicenote.ui.theme.AlertOrange
import com.example.voicenote.ui.theme.PrimaryBlue
import com.example.voicenote.ui.theme.TextSecondary

@OptIn(Material3Api::class)
@Composable
fun NoteInsightsScreen(
    viewModel: NoteViewModel,
    noteId: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load note detail if not already loaded or if id changed
    LaunchedEffect(noteId) {
        viewModel.loadNoteDetail(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Insights", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←", color = Color.White, fontSize = 24.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is NoteDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NoteDetailState.Success -> {
                    NoteInsightsContent(state.note)
                }
                is NoteDetailState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun NoteInsightsContent(note: NoteDetailResponse) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Conflict Banner (if any)
        if (!note.conflicts.isNullOrEmpty()) {
            item {
                ConflictBanner(note.conflicts[0])
            }
        }

        // 2. Note Header
        item {
            Column {
                Text(note.title, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Feb 15, 2026 • 10:30 AM", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        // 3. Summary Section
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("AI Summary", style = MaterialTheme.typography.titleMedium, color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(note.summary, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
        }

        // 4. AI Key Points (Carousel)
        item {
            Text("Key Points", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            KeyPointsCarousel(note.keyPoints)
        }

        // 5. Transcript Section
        item {
            Text("Transcript", style = MaterialTheme.typography.titleLarge, color = Color.White)
        }

        items(note.transcript) { segment ->
            TranscriptSegmentRow(segment)
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@Composable
fun ConflictBanner(conflict: Conflict) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 12.dp
    ) {
        Row(
            modifier = Modifier
                .background(AlertOrange.copy(alpha = 0.1f))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Conflict Detected", style = MaterialTheme.typography.titleSmall, color = AlertOrange)
                Text(
                    "Contradicts: \"${conflict.sourceNoteTitle}\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Text(conflict.description, style = MaterialTheme.typography.bodySmall, color = Color.White)
            }
        }
    }
}

@Composable
fun KeyPointsCarousel(keyPoints: com.example.voicenote.data.models.KeyPoints) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(keyPoints.actionItems) { item ->
            KeyPointCard("Action Item", "✅", item, Color(0xFF49CC90))
        }
        items(keyPoints.decisions) { item ->
            KeyPointCard("Decision", "🎯", item, Color(0xFFFCA130))
        }
        items(keyPoints.insights) { item ->
            KeyPointCard("Insight", "💡", item, PrimaryBlue)
        }
    }
}

@Composable
fun KeyPointCard(type: String, icon: String, text: String, color: Color) {
    GlassCard(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(type, style = MaterialTheme.typography.labelMedium, color = color)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium, color = Color.White, maxLines = 3)
        }
    }
}

@Composable
fun TranscriptSegmentRow(segment: TranscriptSegment) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(24.dp),
                shape = CircleShape,
                color = if (segment.speaker == "Speaker 1") PrimaryBlue else Color.Gray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(segment.speaker?.lastOrNull()?.toString() ?: "?", fontSize = 12.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(segment.speaker ?: "Unknown", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(formatTime(segment.start), style = MaterialTheme.typography.labelSmall, color = TextSecondary.copy(alpha = 0.5f))
        }
        Text(
            segment.text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp, start = 32.dp)
        )
    }
}

fun formatTime(seconds: Float): String {
    val mins = (seconds / 60).toInt()
    val secs = (seconds % 60).toInt()
    return String.format("%02d:%02d", mins, secs)
}
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
