package com.abstudio.voicenote.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 12,
    color: Color = PrimaryBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    
    val animatedHeights = (0 until barCount).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = when {
                        index % 4 == 0 -> 900
                        index % 3 == 0 -> 1300
                        index % 2 == 0 -> 1100
                        else -> 800
                    },
                    easing = EaseInOut
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }
    
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = (canvasWidth / barCount) * 0.6f
        val spacing = (canvasWidth / barCount) * 0.4f
        
        animatedHeights.forEachIndexed { index, animatedHeight ->
            val height = if (isPlaying) {
                canvasHeight * animatedHeight.value
            } else {
                canvasHeight * 0.2f
            }
            
            val x = index * (barWidth + spacing) + spacing / 2
            val y = (canvasHeight - height) / 2
            
            drawRoundRect(
                color = color.copy(alpha = if (isPlaying) 1f else 0.5f),
                topLeft = Offset(x, y),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}
