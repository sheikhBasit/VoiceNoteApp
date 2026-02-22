package com.abstudio.voicenote.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun WorkspaceSetupScreen(
    onBack: () -> Unit,
    onJoinWorkspace: (String) -> Unit,
    onCreateWorkspace: () -> Unit
) {
    var orgCode by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIos, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Text(
                    "Workspace Setup",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(48.dp))
            }

            // Hero
            Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp)) {
                Box(
                    modifier = Modifier.size(64.dp).background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardVoice, "Icon", tint = PrimaryBlue, modifier = Modifier.size(32.dp))
                }
                Text("Join or Create Workspace", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 24.dp))
                Text(
                    "Enter your organization's unique code to join your team, or start a new workspace for your company.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Form
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column {
                    Text("ORGANIZATION CODE", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = orgCode,
                        onValueChange = { orgCode = it.uppercase() },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        placeholder = { Text("E.G., VN-12345", color = Color.Gray.copy(alpha = 0.5f)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                            focusedContainerColor = Color.White.copy(alpha = 0.05f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedBorderColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Button(
                    onClick = { onJoinWorkspace(orgCode) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Join Workspace", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                
                TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Need help finding your code?", color = PrimaryBlue, fontSize = 14.sp)
                }
            }

            // Divider
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 24.dp)) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                Text("OR", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color.White.copy(alpha = 0.1f)))
            }

            // Create Section
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AddBusiness, "New", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("New Organization?", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(
                                "Set up a secure, AI-powered workspace for your entire company in minutes.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onCreateWorkspace,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Text("Create New Organization", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
