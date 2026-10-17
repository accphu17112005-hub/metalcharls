package org.metalscope.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.MetalType
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalPalladium
import org.metalscope.app.theme.MetalPlatinum
import org.metalscope.app.theme.MetalSilver
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.util.CurrencyFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ComparisonChart(
    metalData: Map<MetalType, List<HistoricalRecord>>,
    modifier: Modifier = Modifier
) {
    if (metalData.isEmpty()) return

    // Track active enabled metals in comparison
    var enabledMetals by remember {
        mutableStateOf(setOf(MetalType.GOLD, MetalType.SILVER, MetalType.PLATINUM, MetalType.PALLADIUM))
    }

    val activeSeries = metalData.filterKeys { enabledMetals.contains(it) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Mode Header & Legend Toggles
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MetalType.entries.forEach { metal ->
                val isSelected = enabledMetals.contains(metal)
                val color = metal.defaultColor

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            if (isSelected) color.copy(alpha = 0.15f) else MetalTheme.colors.elevated,
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) color else MetalTheme.colors.border,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            enabledMetals = if (isSelected) {
                                if (enabledMetals.size > 1) enabledMetals - metal else enabledMetals
                            } else {
                                enabledMetals + metal
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = metal.symbol,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MetalTheme.colors.textPrimary else MetalTheme.colors.textSecondary
                    )
                }
            }
        }

        // Subtitle note
        Text(
            text = "Normalized % Performance (Baseline 0.0%)",
            style = MaterialTheme.typography.labelSmall,
            color = MetalTheme.colors.textSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val baselineBorderColor = MetalTheme.colors.border
        val guidelineColor = MetalTheme.colors.border.copy(alpha = 0.4f)

        // Canvas Multi-Line Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(210.dp)) {
                val w = size.width
                val h = size.height

                if (w <= 0f || h <= 32f) return@Canvas

                // Calculate normalized returns for each metal series
                // normalized_pct = ((price - initialPrice) / initialPrice) * 100.0
                val normalizedSeries = activeSeries.mapValues { (_, records) ->
                    if (records.isEmpty()) emptyList()
                    else {
                        val basePrice = records.first().closeUsdPerOz
                        records.map { r ->
                            if (basePrice > 0) ((r.closeUsdPerOz - basePrice) / basePrice) * 100.0 else 0.0
                        }
                    }
                }

                val allValues = normalizedSeries.values.flatten()
                val minVal = (allValues.minOrNull() ?: -2.0).coerceAtMost(-1.0)
                val maxVal = (allValues.maxOrNull() ?: 2.0).coerceAtLeast(1.0)
                val span = (maxVal - minVal).coerceAtLeast(0.01)

                val highY = 16f
                val lowY = h - 16f
                val minY = minOf(highY, lowY)
                val maxY = maxOf(highY, lowY)

                // Zero baseline Y
                val zeroFraction = ((0.0 - minVal) / span).toFloat()
                val zeroY = lowY - (zeroFraction * (lowY - highY))

                // Draw zero baseline
                drawLine(
                    color = baselineBorderColor,
                    start = Offset(0f, zeroY),
                    end = Offset(w, zeroY),
                    strokeWidth = 1.5.dp.toPx()
                )

                // High / Low guidelines
                val dash = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                drawLine(
                    color = guidelineColor,
                    start = Offset(0f, highY),
                    end = Offset(w, highY),
                    pathEffect = dash,
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = guidelineColor,
                    start = Offset(0f, lowY),
                    end = Offset(w, lowY),
                    pathEffect = dash,
                    strokeWidth = 1.dp.toPx()
                )

                // Draw each metal curve
                normalizedSeries.forEach { (metal, values) ->
                    if (values.size < 2) return@forEach

                    val color = metal.defaultColor
                    val points = values.mapIndexed { i, v ->
                        val x = (i.toFloat() / (values.size - 1)) * w
                        val frac = ((v - minVal) / span).toFloat()
                        val y = lowY - (frac * (lowY - highY))
                        Offset(x, y.coerceIn(minY, maxY))
                    }

                    val path = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cX = (prev.x + curr.x) / 2f
                            cubicTo(cX, prev.y, cX, curr.y, curr.x, curr.y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}
