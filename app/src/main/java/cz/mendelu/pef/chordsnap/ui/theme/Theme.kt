package cz.mendelu.pef.chordsnap.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = SurfaceWhite,
    primaryContainer = CardBackgroundLight,
    onPrimaryContainer = TextPrimary,

    secondary = AccentCyan,
    onSecondary = SurfaceWhite,
    secondaryContainer = CardBackgroundLight,
    onSecondaryContainer = TextPrimary,

    tertiary = GradientStartLight,
    onTertiary = SurfaceWhite,
    tertiaryContainer = GradientEndLight,
    onTertiaryContainer = SurfaceWhite,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SearchFieldLight,
    onSurfaceVariant = TextSecondary,

    error = ErrorLight,
    onError = SurfaceWhite,

    outline = TextUnselected,
    outlineVariant = CardBackgroundLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = SurfaceWhite,
    primaryContainer = CardBackgroundDark,
    onPrimaryContainer = SurfaceWhite,

    secondary = AccentCyan,
    onSecondary = BackgroundDark,
    secondaryContainer = CardBackgroundDark,
    onSecondaryContainer = SurfaceWhite,

    tertiary = GradientStartDark,
    onTertiary = SurfaceWhite,
    tertiaryContainer = GradientEndDark,
    onTertiaryContainer = SurfaceWhite,

    background = BackgroundDark,
    onBackground = SurfaceWhite,

    surface = CardBackgroundDark,
    onSurface = SurfaceWhite,
    surfaceVariant = SearchFieldDark,
    onSurfaceVariant = TextSecondary,

    error = ErrorDark,
    onError = BackgroundDark,

    outline = TextUnselected,
    outlineVariant = CardBackgroundDark
)

@Composable
fun ChordSnapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}