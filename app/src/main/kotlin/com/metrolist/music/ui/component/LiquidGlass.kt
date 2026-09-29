package com.metrolist.music.ui.component

import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

private const val TAG = "LiquidGlass"

/**
 * Applies a liquid glass effect to the component.
 * On API 31+, this uses a blur effect with a safe fallback for devices whose GPU
 * doesn't support RenderEffect (e.g. some vivo, OPPO, Realme devices that throw
 * IllegalArgumentException: "nativePtr is null").
 * On older APIs, it falls back to a semi-transparent scrim.
 */
fun Modifier.liquidGlass(
    scrimColor: Color = Color.Black.copy(alpha = 0.85f),
    blurRadius: Float = 30f
): Modifier = composed {
    val glassModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0f) {
        // Create the RenderEffect once during composition to avoid native memory exhaustion
        // and "nativePtr is null" crashes caused by recreating it on every frame inside graphicsLayer.
        val composeBlurEffect = androidx.compose.runtime.remember(blurRadius) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    android.graphics.RenderEffect.createBlurEffect(
                        blurRadius,
                        blurRadius,
                        android.graphics.Shader.TileMode.CLAMP
                    ).asComposeRenderEffect()
                } catch (e: Exception) {
                    // Some devices (vivo, OPPO, etc.) crash with "nativePtr is null"
                    // Gracefully skip the blur
                    Log.w(TAG, "RenderEffect.createBlurEffect failed on this device, skipping blur", e)
                    null
                }
            } else null
        }

        if (composeBlurEffect != null) {
            this.graphicsLayer {
                renderEffect = composeBlurEffect
                clip = true
            }.background(scrimColor)
        } else {
            this.background(scrimColor)
        }
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

