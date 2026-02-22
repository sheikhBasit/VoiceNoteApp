package com.abstudio.voicenote.features.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abstudio.voicenote.data.models.Conflict
import com.abstudio.voicenote.data.models.NoteDetailResponse
import com.abstudio.voicenote.data.models.NoteTask
import com.abstudio.voicenote.core.ui.components.GlassCard
import com.abstudio.voicenote.core.ui.theme.AlertOrange
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import com.abstudio.voicenote.core.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteInsightsScreen(
    viewModel: NoteViewModel,
    noteId: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

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
            when (val state = detailState) {
                is NoteDetailState.Idle -> {
                    // Do nothing
                }
                is NoteDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is NoteDetailState.Success -> {
                    NoteInsightsContent(state.note, viewModel)
                }
                is NoteDetailState.Error -> {
                    Text(state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }
        }
    }
}

@Composable
fun NoteInsightsContent(note: NoteDetailResponse, viewModel: NoteViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Conflict Banner (if any)
        if (!note.conflicts.isNullOrEmpty()) {
            item {
                ConflictBanner(note.conflicts[0])
            }
        }

        // 2. AI Key Points Section
        item {
            Text(
                "✨ AI Key Points",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                com.abstudio.voicenote.core.ui.components.AIKeyPointCard(
                    keyPoint = com.abstudio.voicenote.data.models.AIKeyPoint(
                        type = com.abstudio.voicenote.data.models.AIKeyPointType.ACTION,
                        text = "Revise Q4 budget by Friday"
                    )
                )
                com.abstudio.voicenote.core.ui.components.AIKeyPointCard(
                    keyPoint = com.abstudio.voicenote.data.models.AIKeyPoint(
                        type = com.abstudio.voicenote.data.models.AIKeyPointType.DECISION,
                        text = "Pivot marketing to LinkedIn focus"
                    )
                )
                com.abstudio.voicenote.core.ui.components.AIKeyPointCard(
                    keyPoint = com.abstudio.voicenote.data.models.AIKeyPoint(
                        type = com.abstudio.voicenote.data.models.AIKeyPointType.INSIGHT,
                        text = "Competitor X is lowering prices"
                    )
                )
            }
        }

        // 3. Note Header
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

        // 4. Semantic Insights
        item {
            Text("AI Insights", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            note.semanticAnalysis?.let { analysis ->
                SemanticInsightsRow(analysis)
            }
        }

        // 4.1 Business Leads (NEW)
        if (!note.businessLeads.isNullOrEmpty()) {
            item {
                Text("💼 Business Leads", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(note.businessLeads) { lead ->
                BusinessLeadCard(lead)
            }
        }

        // 4.2 Related Notes (NEW)
        if (!note.relatedNotes.isNullOrEmpty()) {
            item {
                Text("🔗 Semantic Links", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Text("Notes with similar context", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(note.relatedNotes) { related ->
                        RelatedNoteCard(related, onClick = {
                            viewModel.loadNoteDetail(related.id)
                        })
                    }
                }
            }
        }

        // 5. Action Items (Tasks)
        if (!note.tasks.isNullOrEmpty()) {
            item {
                Text("Action Items", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
            }
            items(note.tasks) { task ->
                TaskCard(task)
            }
        }

        // 6. Transcript Section
        item {
            Text("Full Transcript", style = MaterialTheme.typography.titleLarge, color = Color.White)
            Spacer(modifier = Modifier.height(12.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    note.transcript,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(16.dp)
                )
            }
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
fun SemanticInsightsRow(analysis: com.abstudio.voicenote.data.models.NoteSemanticAnalysis) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SentimentCard(analysis.sentiment, analysis.emotionalTone)
        }
        items(analysis.keyInsights) { insight ->
            InsightCard(insight)
        }
    }
}

@Composable
fun SentimentCard(sentiment: String, tone: String) {
    GlassCard(modifier = Modifier.width(200.dp).height(100.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Sentiment", style = MaterialTheme.typography.labelMedium, color = PrimaryBlue)
            Text(sentiment, style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(tone, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
fun InsightCard(insight: String) {
    GlassCard(modifier = Modifier.width(240.dp).height(100.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Insight", style = MaterialTheme.typography.labelMedium, color = Color(0xFF49CC90))
            Text(insight, style = MaterialTheme.typography.bodyMedium, color = Color.White, maxLines = 3)
        }
    }
}

@Composable
fun TaskCard(task: NoteTask) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        cornerRadius = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = when(task.priority) {
                "CRITICAL" -> Color.Red
                "HIGH" -> AlertOrange
                else -> PrimaryBlue
            }
            Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = color) {}
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(task.description, style = MaterialTheme.typography.bodyLarge, color = Color.White)
                if (task.title != null) {
                    Text(task.title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun BusinessLeadCard(lead: com.abstudio.voicenote.data.models.BusinessLead) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👤", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(lead.name, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        lead.prospectType.uppercase(),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(lead.context, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            if (!lead.contactInfo.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("📞 ${lead.contactInfo}", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
            }
        }
    }
}

@Composable
fun RelatedNoteCard(
    related: com.abstudio.voicenote.data.models.RelatedNote,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick() },
        cornerRadius = 12.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("📄 RELATED", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                related.title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("View Details ›", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

fun formatTime(seconds: Float): String {
    val mins = (seconds / 60).toInt()
    val secs = (seconds % 60).toInt()
    return String.format("%02d:%02d", mins, secs)
}
