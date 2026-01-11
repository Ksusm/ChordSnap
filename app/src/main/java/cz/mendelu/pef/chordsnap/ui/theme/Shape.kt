package cz.mendelu.pef.chordsnap.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
object CustomShapes {
    val practiceCard = RoundedCornerShape(30.dp)
    val chordCard = RoundedCornerShape(25.dp)
    val mainImage = RoundedCornerShape(28.dp)
    val fab = RoundedCornerShape(16.dp)
    val searchField = RoundedCornerShape(20.dp)
}