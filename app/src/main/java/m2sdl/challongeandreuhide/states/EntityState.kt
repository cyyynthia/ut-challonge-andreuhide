package m2sdl.challongeandreuhide.states

import androidx.compose.ui.geometry.Offset

class EntityState(
	var position: Offset,
	var health: Float,
) {
	val isAlive: Boolean
		get() = health > 0f
}
