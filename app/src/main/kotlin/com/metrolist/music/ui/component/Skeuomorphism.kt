package com.metrolist.music.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Applies a skeuomorphic button effect with 3D drop shadows and a press-down scale animation.
 */
fun Modifier.skeuomorphicButton(
    interactionSource: InteractionSource
): Modifier = composed {
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Scale down slightly when pressed to simulate physical depression
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, label = "buttonScale")
    
    // Outer shadow for 3D pop effect (disabled/inverted when pressed)
    val shadowAlpha = if (isPressed) 0f else 0.5f

    this.then(
        Modifier
            .scale(scale)
            .graphicsLayer {
                shadowElevation = if (isPressed) 0f else 8f
                ambientShadowColor = Color.Black.copy(alpha = shadowAlpha)
                spotShadowColor = Color.Black.copy(alpha = shadowAlpha)
            }
    )
}
