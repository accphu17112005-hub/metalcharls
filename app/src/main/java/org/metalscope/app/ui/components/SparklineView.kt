package org.metalscope.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive

@Composable
fun SparklineView(
    data: List<Double>,
    modifier: Modifier = Modifier,
    isPositive: Boolean = true,
    showFill: Boolean = true
) {
    if (data.size < 2) return

    val strokeColor = if (isPositive) ChangePositive else ChangeNegative
    val fillColor = strokeColor.copy(alpha = 0.15f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        if (width <= 0f || height <= 4f) return@Canvas

        val minVal = data.minOrNull() ?: 0.0
        val maxVal = data.maxOrNull() ?: 1.0
        val range = (maxVal - minVal).coerceAtLeast(0.0001)

        val minY = 2f
        val maxY = (height - 2f).coerceAtLeast(minY)

        val points = data.mapIndexed { index, value ->
            val x = (index.toFloat() / (data.size - 1)) * width
            val y = height - (((value - minVal) / range).toFloat() * height)
            androidx.compose.ui.geometry.Offset(x, y.coerceIn(minY, maxY))
        }

        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val current = points[i]
                val cX = (prev.x + current.x) / 2f
                cubicTo(cX, prev.y, cX, current.y, current.x, current.y)
            }
        }

        if (showFill) {
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillColor, Color.Transparent),
                    startY = 0f,
                    endY = height
                )
            )
        }

        drawPath(
            path = strokePath,
            color = strokeColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}
