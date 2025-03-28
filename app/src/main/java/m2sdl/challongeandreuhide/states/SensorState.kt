package m2sdl.challongeandreuhide.states

class SensorState {
	// Provided by Activity
	// Read by SensorThread
	var lastAmbientLight = 0f
	var lastAccelerometer = Triple(0f, 0f, 0f)
	var currentAccelerometer = Triple(0f, 0f, 0f)

	// Provided by SensorThread
	// Read by GameThread (destructive read)
	var noise: SensorNoise? = null
		get() {
			val v = field
			field = null
			return v
		}

	var shaking = false

	// Computed
	val effectiveLight: Float
		get() = ((lastAmbientLight - 20f) / 80f).coerceIn(0f, 1f)

	enum class SensorNoise {
		Talking,
		Yelling,
		Screaming,
	}
}
