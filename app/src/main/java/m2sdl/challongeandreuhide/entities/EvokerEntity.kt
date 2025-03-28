package m2sdl.challongeandreuhide.entities

import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.R

class EvokerEntity(position: Offset) : AbstractMobEntity(
	position = position,
	resource = R.drawable.sprite_evoker,
	initialHealth = 20,
	width = 80,
	strength = 10,
	speed = 2f
)
