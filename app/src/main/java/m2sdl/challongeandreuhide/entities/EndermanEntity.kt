package m2sdl.challongeandreuhide.entities

import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.R

class EndermanEntity(position: Offset) : AbstractMobEntity(
	position = position,
	resource = R.drawable.sprite_enderman,
	initialHealth = 35,
	width = 80,
	strength = 10,
	speed = 2f
)
