/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSliderThumb(
    sliderState: SliderState,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    thumbSize: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(thumbSize)
            .shadow(4.dp, RoundedCornerShape(50), clip = false)
            .background(
                brush = androidx.compose.ui.graphics.Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color.LightGray
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .border(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.8f),
                        Color.Black.copy(alpha = 0.2f)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
    ) {
        // Inner concentric circle to make it look like a physical knob/disc
        Box(
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.Center)
                .size(thumbSize * 0.4f)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.radialGradient(
                        colors = listOf(
                            Color.LightGray.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSliderTrack(
    sliderState: SliderState,
    modifier: Modifier = Modifier,
    colors: SliderColors = SliderDefaults.colors(),
    trackHeight: Dp = 12.dp
) {
    val inactiveTrackColor = colors.inactiveTrackColor
    val activeTrackColor = colors.activeTrackColor
    val inactiveTickColor = colors.inactiveTickColor
    val activeTickColor = colors.activeTickColor
    val valueRange = sliderState.valueRange
    Canvas(
        modifier
            .fillMaxWidth()
            .height(trackHeight)
    ) {
        drawTrack(
            stepsToTickFractions(sliderState.steps),
            0f,
            calcFraction(
                valueRange.start,
                valueRange.endInclusive,
                sliderState.value.coerceIn(valueRange.start, valueRange.endInclusive)
            ),
            inactiveTrackColor,
            activeTrackColor,
            inactiveTickColor,
            activeTickColor,
            trackHeight
        )
    }
}

private fun DrawScope.drawTrack(
    tickFractions: FloatArray,
    activeRangeStart: Float,
    activeRangeEnd: Float,
    inactiveTrackColor: Color,
    activeTrackColor: Color,
    inactiveTickColor: Color,
    activeTickColor: Color,
    trackHeight: Dp = 2.dp
) {
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val sliderLeft = Offset(0f, center.y)
    val sliderRight = Offset(size.width, center.y)
    val sliderStart = if (isRtl) sliderRight else sliderLeft
    val sliderEnd = if (isRtl) sliderLeft else sliderRight
    val tickSize = 2.0.dp.toPx()
    val trackStrokeWidth = trackHeight.toPx()
    
    // Draw inset background (groove)
    drawLine(
        color = Color.Black.copy(alpha = 0.3f), // Shadow inside the groove
        start = sliderStart.copy(y = sliderStart.y - 1f),
        end = sliderEnd.copy(y = sliderEnd.y - 1f),
        strokeWidth = trackStrokeWidth,
        cap = StrokeCap.Round
    )
    drawLine(
        color = inactiveTrackColor,
        start = sliderStart,
        end = sliderEnd,
        strokeWidth = trackStrokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
    
    val sliderValueEnd = Offset(
        sliderStart.x +
                (sliderEnd.x - sliderStart.x) * activeRangeEnd,
        center.y
    )
    val sliderValueStart = Offset(
        sliderStart.x +
                (sliderEnd.x - sliderStart.x) * activeRangeStart,
        center.y
    )
    
    // Draw active track
    drawLine(
        color = activeTrackColor,
        start = sliderValueStart,
        end = sliderValueEnd,
        strokeWidth = trackStrokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
    // Add highlight to active track for a raised/liquid feel inside the groove
    drawLine(
        color = Color.White.copy(alpha = 0.4f),
        start = sliderValueStart.copy(y = sliderValueStart.y - 2f),
        end = sliderValueEnd.copy(y = sliderValueEnd.y - 2f),
        strokeWidth = trackStrokeWidth * 0.2f,
        cap = StrokeCap.Round
    )
    
    for (tick in tickFractions) {
        val outsideFraction = tick > activeRangeEnd || tick < activeRangeStart
        drawCircle(
            color = if (outsideFraction) inactiveTickColor else activeTickColor,
            center = Offset(lerp(sliderStart, sliderEnd, tick).x, center.y),
            radius = tickSize / 2f
        )
    }
}

private fun stepsToTickFractions(steps: Int): FloatArray {
    return if (steps == 0) floatArrayOf() else FloatArray(steps + 2) { it.toFloat() / (steps + 1) }
}

private fun calcFraction(a: Float, b: Float, pos: Float) =
    (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)
