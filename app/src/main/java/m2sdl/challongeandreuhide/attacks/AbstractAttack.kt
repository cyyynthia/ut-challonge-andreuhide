package m2sdl.challongeandreuhide.attacks

abstract class AbstractAttack {
	abstract val resetValue: Int
	abstract val damages: Float


	abstract fun fire(): Float
}
