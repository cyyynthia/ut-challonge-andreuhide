package m2sdl.challongeandreuhide.entities

import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.R

class TowerEntity(position: Offset) : AbstractEntity(
	position = position,
	resource = R.drawable.sprite_tower,
	initialHealth = 100,
	width = 200,
)
