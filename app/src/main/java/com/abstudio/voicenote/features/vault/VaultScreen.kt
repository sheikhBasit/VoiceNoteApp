package com.abstudio.voicenote.features.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.abstudio.voicenote.core.ui.components.GlassCard
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import com.abstudio.voicenote.core.ui.theme.TextSecondary
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultScreen(
    onNoteClick: (String) -> Unit,
    onFolderClick: (String) -> Unit,
    viewModel: NoteViewModel = hiltViewModel(),
    bootstrapViewModel: com.abstudio.voicenote.core.ui.viewmodel.GlobalBootstrapViewModel = hiltViewModel(),
    biometricHelper: com.abstudio.voicenote.core.util.BiometricHelper? = null
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.notes) {
        if (uiState.notes.isNotEmpty()) {
            bootstrapViewModel.onEnterVault(uiState.notes.map { it.id })
        }
    }
    var isAuthenticated by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    var searchQuery by remember { mutableStateOf("") } // Keep local state for Text field, sync via onValueChange

    // Auth Check
    LaunchedEffect(Unit) {
        if (biometricHelper != null && context is androidx.fragment.app.FragmentActivity) {
            isAuthenticated = false
            // Check capability first
            val canAuth = biometricHelper.canAuthenticate(context)
            if (canAuth == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS) {
                 val success = biometricHelper.authenticate(context)
                 isAuthenticated = success
            } else {
                // If incompatible or no hardware, just allow access (or handle error)
                // For MVP, allow access if no biometrics
                isAuthenticated = true
            }
        } else {
             // Preview or testing logic
             isAuthenticated = true
        }
    }

    if (!isAuthenticated) {
        // Locked State
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text(
                    "Vault Locked", 
                    style = MaterialTheme.typography.titleLarge, 
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Authenticating...", 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = com.abstudio.voicenote.core.ui.theme.TextSecondary
                )
            }
        }
    } else {
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
                        onValueChange = { 
                            searchQuery = it
                            viewModel.onSearchQueryChanged(it)
                        },
                        placeholder = { Text("Search meetings, topics, or insights...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            focusedContainerColor = Color.White.copy(alpha = 0.05f)
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
                if (uiState.isLoading && uiState.notes.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                        color = PrimaryBlue
                    )
                } else if (uiState.notes.isEmpty()) {
                    Text(
                        "No notes found.", 
                        color = TextSecondary, 
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                "Shared Folders",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 8.dp)
                            ) {
                                items(listOf("Marketing Sync", "Product Design", "Client Feedbacks")) { folder ->
                                    Card(
                                        onClick = { onFolderClick(folder) },
                                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.width(160.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.FolderShared, contentDescription = null, tint = PrimaryBlue)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(folder, style = MaterialTheme.typography.labelMedium, color = Color.White, maxLines = 1)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                        Text(
                            "All Notes",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(uiState.notes) { note ->
                             Card(
                                onClick = { onNoteClick(note.id) },
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                                    Text(note.title, style = MaterialTheme.typography.titleMedium, color = Color.White)
                                    Text(
                                        note.summary.take(50) + "...", 
                                        style = MaterialTheme.typography.bodySmall, 
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
                
                uiState.error?.let { errorMessage ->
                    Text(
                        errorMessage,
                        color = Color.Red,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter).padding(16.dp)
                    )
                }
            }
        }
    }
}
