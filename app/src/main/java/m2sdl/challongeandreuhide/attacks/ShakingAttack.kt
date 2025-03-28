package m2sdl.challongeandreuhide.attacks

class ShakingAttack : AbstractAttack() {
	override val resetValue: Int
		get() = 5000

	override val damages: Float
		get() = 10f

	var initCooldown: Long = 0

	override fun fire(): Float {
		val now = System.currentTimeMillis()
		if (now - initCooldown > resetValue) {
			initCooldown = System.currentTimeMillis()
			return damages
		}
		return 0f
	}
}
