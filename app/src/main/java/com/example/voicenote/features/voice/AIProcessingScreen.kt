package com.example.voicenote.features.voice

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.theme.PrimaryBlue

@Composable
fun AIProcessingScreen(
    fileName: String,
    progress: Float,
    onMinimize: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, "AI", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Analyzing Audio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(fileName, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
                Surface(
                    color = PrimaryBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.clickable { onMinimize() }
                ) {
                    Text("Minimize", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = PrimaryBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Progress
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("Processing insights...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(top = 8.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .background(PrimaryBlue, CircleShape)
                    )
                }
            }

            // Skeletal Content
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                item { ProcessingSection("EXECUTIVE SUMMARY", Icons.Default.Subject) }
                item { ProcessingSection("KEY TAKEAWAYS", Icons.Default.Lightbulb, isList = true) }
                item { ProcessingSection("ACTION ITEMS", Icons.Default.Checklist, isChecklist = true) }
                
                item { 
                    Spacer(modifier = Modifier.height(24.dp))
                    ProTipBanner()
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun ProcessingSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isList: Boolean = false, isChecklist: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
        }
        
        if (isList) {
            repeat(3) { ShimmerLine(widthFraction = 0.8f + (it * 0.05f), hasBullet = true) }
        } else if (isChecklist) {
            repeat(2) { ShimmerLine(widthFraction = 0.6f + (it * 0.1f), hasCheckbox = true) }
        } else {
            repeat(3) { ShimmerLine(widthFraction = 0.85f + (it * 0.05f)) }
        }
    }
}

@Composable
fun ShimmerLine(widthFraction: Float, hasBullet: Boolean = false, hasCheckbox: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (hasBullet) Box(modifier = Modifier.size(6.dp).background(PrimaryBlue.copy(alpha = 0.4f), CircleShape))
        if (hasCheckbox) Box(modifier = Modifier.size(20.dp).border(2.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(4.dp)))
        
        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .height(12.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.03f))
        )
    }
}

@Composable
fun ProTipBanner() {
    Surface(
        color = PrimaryBlue.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.TipsAndUpdates, "Tip", tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
            Column {
                Text("Pro Tip", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    "Tag colleagues in your voice notes to automatically assign action items to their Slack profile.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.width(20.dp).height(4.dp).background(PrimaryBlue, CircleShape))
                    Box(modifier = Modifier.width(6.dp).height(4.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                    Box(modifier = Modifier.width(6.dp).height(4.dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
                }
            }
        }
    }
}
