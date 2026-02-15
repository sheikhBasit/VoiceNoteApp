package com.example.voicenote.features.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.theme.PrimaryBlue

@Composable
fun FolderAccessControlScreen(
    folderName: String,
    onBack: () -> Unit,
    onAddMember: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIos, "Back", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(folderName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Folder Permissions", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                IconButton(onClick = onAddMember) {
                    Icon(Icons.Default.PersonAdd, "Add", tint = PrimaryBlue)
                }
            }

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search team members...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                    focusedContainerColor = Color.White.copy(alpha = 0.05f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // AI Insight
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .border(1.dp, PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Lightbulb, "Insight", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AI INSIGHT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
                        Text(
                            "3 members haven't accessed this folder in 30 days. Consider revoking their access to maintain security.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Text("Review", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Member List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        "12 MEMBERS WITH ACCESS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(sampleMembers) { member ->
                    MemberAccessItem(member)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .clickable { }
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Box(modifier = Modifier.size(36.dp).background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                Text("🔗", color = PrimaryBlue)
                            }
                            Column {
                                Text("Public Access Link", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Currently disabled for this folder", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("›", color = Color.Gray, fontSize = 20.sp)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun MemberAccessItem(member: AccessMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.size(44.dp).background(Color.Gray.copy(alpha = 0.2f), CircleShape))
        Column(modifier = Modifier.weight(1f)) {
            Text(member.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (member.inactive) Color.White.copy(alpha = 0.5f) else Color.White)
            if (member.inactive) {
                Text("INACTIVE 45 DAYS", color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
            } else {
                Text(member.email, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
        Surface(
            color = if (member.role == "Editor") PrimaryBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable { }
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(member.role, color = if (member.role == "Editor") PrimaryBlue else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ExpandMore, "Options", tint = Color.Gray, modifier = Modifier.size(16.dp))
            }
        }
    }
}

data class AccessMember(val name: String, val email: String, val role: String, val inactive: Boolean = false)

val sampleMembers = listOf(
    AccessMember("Sarah Jenkins (You)", "sarah.j@voicecorp.ai", "Owner"),
    AccessMember("Michael Chen", "m.chen@voicecorp.ai", "Editor"),
    AccessMember("Elena Rodriguez", "elena.r@voicecorp.ai", "Viewer"),
    AccessMember("James David", "j.david@voicecorp.ai", "Viewer", inactive = true),
    AccessMember("David Kim", "d.kim@voicecorp.ai", "Editor")
)
