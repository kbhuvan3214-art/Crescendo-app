package com.metrolist.music.ui.component

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Applies a liquid glass effect to the component.
 * On API 31+, this uses a blur effect. On older APIs, it falls back to a semi-transparent scrim.
 */
fun Modifier.liquidGlass(
    scrimColor: Color = Color.Black.copy(alpha = 0.4f),
    blurRadius: Float = 30f
): Modifier = composed {
    val glassModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        this.graphicsLayer {
            val blurEffect = android.graphics.RenderEffect.createBlurEffect(
                blurRadius,
                blurRadius,
                android.graphics.Shader.TileMode.CLAMP
            )
            renderEffect = blurEffect.asComposeRenderEffect()
            clip = true
        }.background(scrimColor.copy(alpha = 0.2f))
    } else {
        this.background(scrimColor)
    }
    
    // Add refractive top-edge highlight
    glassModifier.drawWithContent {
        drawContent()
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.0f),
                    Color.White.copy(alpha = 0.4f),
                    Color.White.copy(alpha = 0.0f)
                )
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 2.dp.toPx()
        )
    }
}
