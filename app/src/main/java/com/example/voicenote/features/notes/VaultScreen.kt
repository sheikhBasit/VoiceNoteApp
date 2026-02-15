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
    onNoteClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

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
        LazyVerticalGrid(
            columns = GridCells.Fixed(1), // List view for professional look
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mocking list items for now
            items(10) { index ->
                NoteCard(
                    title = if (index % 2 == 0) "Product Sync #$index" else "Client Interview $index",
                    status = if (index < 3) "PROCESSED" else "SYNCED"
                )
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.voicenote.ui.theme.PrimaryBlue
