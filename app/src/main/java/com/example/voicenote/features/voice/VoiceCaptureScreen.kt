package com.example.voicenote.features.voice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voicenote.ui.theme.PrimaryBlue
import com.example.voicenote.ui.theme.PrimaryBlueVariant

@Composable
fun VoiceCaptureScreen(
    onStop: () -> Unit
) {
    var isRecording by remember { mutableStateOf(true) }
    val infiniteTransition = rememberInfiniteTransition()
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Recording Intelligence...",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                "AI is following the conversation",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 8.dp, bottom = 64.dp)
            )

            // Pulse Animation
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulseScale),
                    shape = CircleShape,
                    color = PrimaryBlue.copy(alpha = 0.2f)
                ) {}
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = PrimaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🎙️", fontSize = 32.sp)
                    }
                }
            }

            Text(
                "00:42", // Mock timer
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(top = 48.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onStop,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .size(80.dp),
                shape = CircleShape
            ) {
                Text("⏹️", fontSize = 24.sp)
            }
        }
    }
}
