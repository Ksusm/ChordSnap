package cz.mendelu.pef.chordsnap.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object GradientUtils {
    fun bottomBarGradient(
        startColor: Color = GradientStart,
        endColor: Color = GradientEnd
    ): Brush {
        return Brush.verticalGradient(
            colors = listOf(startColor, endColor),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }

    fun practiceNameGradient(
        startColor: Color = GradientStart,
        endColor: Color = GradientEnd
    ): Brush {
        return Brush.verticalGradient(
            colors = listOf(startColor, endColor)
        )
    }
    fun noteBadgeGradient(
        startColor: Color = GradientStart,
        endColor: Color = GradientEnd
    ): Brush {
        return Brush.verticalGradient(
            colors = listOf(startColor, endColor)
        )
    }
}