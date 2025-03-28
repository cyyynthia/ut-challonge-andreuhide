package m2sdl.challongeandreuhide.entities

import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.R

class RavagerEntity(position: Offset) : AbstractMobEntity(
	position = position,
	resource = R.drawable.sprite_ravager,
	initialHealth = 100,
	width = 110,
	strength = 10,
	speed = 0.5f
)
