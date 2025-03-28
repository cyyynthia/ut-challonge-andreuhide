package m2sdl.challongeandreuhide.states

import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.entities.AbstractMobEntity
import m2sdl.challongeandreuhide.entities.TowerEntity

class GameState(towerOffset: Offset) {
	val tower = TowerEntity(towerOffset)

	val mobs = mutableListOf<AbstractMobEntity>()
}
