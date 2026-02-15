package com.example.voicenote.features.notes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.voicenote.features.dashboard.NoteCard
import com.example.voicenote.ui.theme.TextSecondary

@OptIn(Material3Api::class)
@Composable
fun VaultScreen(
    viewModel: NoteViewModel,
    onNoteClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // In a real app, we'd have a list state in NoteViewModel too
        // For now, let's assume loadNoteDetail(someId) isn't the right one for lists
        // I need a loadNotes() in NoteViewModel
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "The Vault",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search meetings, topics, or insights...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        containerColor = Color.White.copy(alpha = 0.05f)
                    )
                )
                
                Row(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selected = true, onClick = {}, label = { Text("All") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Product") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Client") })
                    FilterChip(selected = false, onClick = {}, label = { Text("Strategy") })
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            // Note: Since I don't have loadNotes() in NoteViewModel yet, 
            // I'll keep the structure but note the need for list state.
            // Wait, I should add loadNotes() to NoteViewModel.
            Text("Notes list will appear here...", color = TextSecondary)
        }
    }
}
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.voicenote.ui.theme.PrimaryBlue
