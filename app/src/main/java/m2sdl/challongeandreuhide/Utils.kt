package m2sdl.challongeandreuhide

import androidx.compose.ui.geometry.Offset
import kotlin.math.pow
import kotlin.math.sqrt

fun Offset.distanceTo(other: Offset): Float {
	val vector = this - other
	return sqrt(vector.x.pow(2) + vector.y.pow(2))
}
