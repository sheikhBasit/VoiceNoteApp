package com.abstudio.voicenote.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.abstudio.voicenote.core.ui.theme.PrimaryBlue

@Composable
fun SimpleLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = PrimaryBlue
) {
    Canvas(modifier = modifier) {
        if (dataPoints.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
        
        val pointSpacing = width / (dataPoints.size - 1).coerceAtLeast(1)
        val path = Path().apply {
            dataPoints.forEachIndexed { index, value ->
                val x = index * pointSpacing
                val y = height - (value / maxVal * height)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
fun SimpleBarChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    barColor: Color = PrimaryBlue
) {
    Canvas(modifier = modifier) {
        if (dataPoints.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val maxVal = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)
        
        val barWidth = width / (dataPoints.size * 2 - 1).coerceAtLeast(1)
        
        dataPoints.forEachIndexed { index, value ->
            val left = index * 2 * barWidth
            val top = height - (value / maxVal * height)
            drawRect(
                color = barColor,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, height - top)
            )
        }
    }
}
