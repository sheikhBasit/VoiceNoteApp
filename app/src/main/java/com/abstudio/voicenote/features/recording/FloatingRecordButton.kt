package com.abstudio.voicenote.features.recording

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abstudio.voicenote.R
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun FloatingRecordButton(
    isRecording: Boolean,
    onRecordClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(scale)
                    .background(Color.Red.copy(alpha = 0.3f), CircleShape)
            )
        }
        
        FloatingActionButton(
            onClick = onRecordClick,
            containerColor = if (isRecording) Color.Red else PrimaryBlue,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            // Icon(
            //     painter = painterResource(id = if (isRecording) R.drawable.ic_stop else R.drawable.ic_mic),
            //     contentDescription = if (isRecording) "Stop" else "Record"
            // )
            // Use Text or vector if drawable missing
             if (isRecording) {
                 // Stop Icon shape
                 Box(modifier = Modifier.size(16.dp).background(Color.White))
             } else {
                 // Mic Icon shape (simplified circle for now)
                 Box(modifier = Modifier.size(16.dp).background(Color.White, CircleShape))
             }
        }
    }
}
