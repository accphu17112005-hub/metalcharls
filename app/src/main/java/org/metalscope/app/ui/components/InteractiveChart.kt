package org.metalscope.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.metalscope.app.data.model.Currency
import org.metalscope.app.data.model.HistoricalRecord
import org.metalscope.app.data.model.TimeRange
import org.metalscope.app.data.model.WeightUnit
import org.metalscope.app.theme.ChangeNegative
import org.metalscope.app.theme.ChangePositive
import org.metalscope.app.theme.MetalGold
import org.metalscope.app.theme.MetalTheme
import org.metalscope.app.util.CurrencyFormatter

@Composable
fun InteractiveChart(
    records: List<HistoricalRecord>,
    currency: Currency,
    unit: WeightUnit,
    selectedTimeRange: TimeRange,
    onTimeRangeSelected: (TimeRange) -> Unit,
    modifier: Modifier = Modifier,
    lineColor: Color = MetalGold
) {
    if (records.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No historical records available",
                style = MaterialTheme.typography.bodyMedium,
                color = MetalTheme.colors.textSecondary
            )
        }
        return
    }

    var selectedIndex by remember(records) { mutableStateOf<Int?>(null) }

    val activeRecord = selectedIndex?.let { records.getOrNull(it) } ?: records.last()
    val activePrice = activeRecord.calculateClose(currency, unit)
    val firstPrice = records.first().calculateClose(currency, unit)
    val priceDiff = activePrice - firstPrice
    val percentDiff = if (firstPrice > 0) (priceDiff / firstPrice) * 100.0 else 0.0
    val isPositive = priceDiff >= 0

    val effectiveColor = if (selectedIndex != null) {
        if (isPositive) ChangePositive else ChangeNegative
    } else {
        lineColor
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Dynamic Chart Header / Tooltip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = CurrencyFormatter.formatPrice(activePrice, currency, unit),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MetalTheme.colors.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = CurrencyFormatter.formatChange(priceDiff, currency),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPositive) ChangePositive else ChangeNegative
                    )
                    Text(
                        text = "(${CurrencyFormatter.formatPercent(percentDiff)})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPositive) ChangePositive else ChangeNegative
                    )
                    Text(
                        text = selectedTimeRange.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MetalTheme.colors.textMuted
                    )
                }
            }

            // Date / Scrub Time tag
            Box(
                modifier = Modifier
                    .background(MetalTheme.colors.elevated, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = activeRecord.dateString,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selectedIndex != null) MetalGold else MetalTheme.colors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Canvas Chart Area
        val prices = records.map { it.calculateClose(currency, unit) }
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 1.0
        val priceSpan = (maxPrice - minPrice).coerceAtLeast(0.001)

        val gridLineColor = MetalTheme.colors.border.copy(alpha = 0.5f)
        val crosshairColor = MetalTheme.colors.textPrimary.copy(alpha = 0.6f)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .pointerInput(records) {
                        detectTapGestures(
                            onPress = { offset ->
                                if (size.width > 0 && records.size > 1) {
                                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                    val idx = (fraction * (records.size - 1)).toInt().coerceIn(0, records.size - 1)
                                    selectedIndex = idx
                                    tryAwaitRelease()
                                    selectedIndex = null
                                }
                            }
                        )
                    }
                    .pointerInput(records) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (size.width > 0 && records.size > 1) {
                                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                    val idx = (fraction * (records.size - 1)).toInt().coerceIn(0, records.size - 1)
                                    selectedIndex = idx
                                }
                            },
                            onDragEnd = { selectedIndex = null },
                            onDragCancel = { selectedIndex = null },
                            onDrag = { change, _ ->
                                if (size.width > 0 && records.size > 1) {
                                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                    val idx = (fraction * (records.size - 1)).toInt().coerceIn(0, records.size - 1)
                                    selectedIndex = idx
                                }
                            }
                        )
                    }
            ) {
                val w = size.width
                val h = size.height

                if (w <= 0f || h <= 32f || prices.size < 2) return@Canvas

                // Draw High / Low reference grid lines
                val highY = 16f
                val lowY = h - 16f
                val midY = (highY + lowY) / 2f
                val minY = minOf(highY, lowY)
                val maxY = maxOf(highY, lowY)
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, highY),
                    end = Offset(w, highY),
                    pathEffect = dashEffect,
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, midY),
                    end = Offset(w, midY),
                    pathEffect = dashEffect,
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, lowY),
                    end = Offset(w, lowY),
                    pathEffect = dashEffect,
                    strokeWidth = 1.dp.toPx()
                )

                // Compute points
                val points = prices.mapIndexed { i, p ->
                    val x = (i.toFloat() / (prices.size - 1)) * w
                    val normalizedY = ((p - minPrice) / priceSpan).toFloat()
                    val y = lowY - (normalizedY * (lowY - highY))
                    Offset(x, y.coerceIn(minY, maxY))
                }

                // Curve path
                val curvePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        val cX = (prev.x + curr.x) / 2f
                        cubicTo(cX, prev.y, cX, curr.y, curr.x, curr.y)
                    }
                }

                // Gradient Fill
                val fillPath = Path().apply {
                    addPath(curvePath)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            effectiveColor.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        startY = highY,
                        endY = h
                    )
                )

                // Main stroke
                drawPath(
                    path = curvePath,
                    color = effectiveColor,
                    style = Stroke(
                        width = 2.5.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // Interactive Crosshair & Scrub Marker
                selectedIndex?.let { idx ->
                    if (idx in points.indices) {
                        val scrubPoint = points[idx]

                        // Vertical Crosshair Line
                        drawLine(
                            color = crosshairColor,
                            start = Offset(scrubPoint.x, 0f),
                            end = Offset(scrubPoint.x, h),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        // Outer glowing circle
                        drawCircle(
                            color = effectiveColor.copy(alpha = 0.35f),
                            radius = 10.dp.toPx(),
                            center = scrubPoint
                        )

                        // Center solid circle
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = scrubPoint
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Time Range Selector Row
        TimeRangeSelector(
            selectedTimeRange = selectedTimeRange,
            onSelect = onTimeRangeSelected
        )
    }
}
