package com.example.voicenote.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.theme.PrimaryBlue

@Composable
fun SystemErrorScreen(
    onBack: () -> Unit,
    onTryAgain: () -> Unit,
    onContactSupport: () -> Unit,
    errorCode: String = "ERR_503_TIMEOUT_RETRY_EXCEEDED"
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Text("SYSTEM ERROR", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Dns, "Node", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Text("NODE_DC_04", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                }
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Illustration
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(listOf(PrimaryBlue.copy(alpha = 0.15f), Color.Transparent))))
                    
                    Surface(
                        modifier = Modifier.size(180.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = PrimaryBlue,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(PrimaryBlue, Color(0xFF60A5FA)))), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, "Robot", tint = Color.White, modifier = Modifier.size(96.dp))
                        }
                    }

                    // Floating items
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 20.dp, end = 10.dp)) {
                        Surface(color = Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
                            Icon(Icons.Default.LinkOff, "LinkOff", tint = Color.Red, modifier = Modifier.padding(8.dp).size(20.dp))
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 20.dp, start = 10.dp)) {
                        Surface(color = Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))) {
                            Icon(Icons.Default.Terminal, "Terminal", tint = PrimaryBlue, modifier = Modifier.padding(8.dp).size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = buildandroidx.compose.ui.text.AnnotatedString("Something ") + buildandroidx.compose.ui.text.AnnotatedString("went sideways", spanStyle = androidx.compose.ui.text.SpanStyle(color = PrimaryBlue)),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    "We're having trouble connecting to our servers. Don't worry, our automated recovery team is already on the case.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Actions
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = onTryAgain,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.Refresh, "Retry")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Try Again", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onContactSupport,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Icon(Icons.Default.SupportAgent, "Support")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Support", fontWeight = FontWeight.Bold)
                    }
                }

                TextButton(onClick = {}, modifier = Modifier.padding(top = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("View system logs", color = Color.Gray, fontSize = 14.sp)
                        Icon(Icons.Default.OpenInNew, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // Footer
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                        Text("ERROR_CODE: $errorCode", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }
                Text(
                    "© 2024 Enterprise Systems Cloud • All Rights Reserved",
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
