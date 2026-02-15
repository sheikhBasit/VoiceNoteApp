package com.example.voicenote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.voicenote.ui.theme.GlassBorder
import com.example.voicenote.ui.theme.GlassWhite

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(GlassWhite)
            .border(1.dp, GlassBorder, RoundedCornerShape(cornerRadius))
    ) {
        content()
    }
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    cornerRadius: Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(colors = colors)
            )
    ) {
        content()
    }
}

@Composable
fun GlowModifier(
    color: Color,
    alpha: Float = 0.2f
) : Modifier {
    return Modifier.background(
        Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent)
        )
    )
}
