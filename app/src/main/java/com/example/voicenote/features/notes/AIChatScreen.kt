package com.example.voicenote.features.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voicenote.ui.theme.PrimaryBlue
import com.example.voicenote.ui.theme.TextSecondary

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(Material3Api::class)
@Composable
fun AIChatScreen() {
    var message by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("Hello! I'm your VoiceNote Assistant. I can answer questions across all your meetings.", false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Global AI Assistant", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(chatMessages) { msg ->
                    ChatBubble(msg)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    placeholder = { Text("Ask about your notes...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                IconButton(
                    onClick = {
                        if (message.isNotEmpty()) {
                            chatMessages.add(ChatMessage(message, true))
                            message = ""
                            // Mock AI Response
                            chatMessages.add(ChatMessage("Searching your notes for information...", false))
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("🚀", fontSize = 24.dp.value.sp) // Custom send icon
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (message.isUser) PrimaryBlue else Color.White.copy(alpha = 0.1f),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                message.text,
                color = Color.White,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
import androidx.compose.ui.unit.sp
