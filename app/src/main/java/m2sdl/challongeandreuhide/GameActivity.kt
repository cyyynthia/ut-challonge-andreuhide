package m2sdl.challongeandreuhide

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import m2sdl.challongeandreuhide.states.SensorState
import m2sdl.challongeandreuhide.views.GameView

class GameActivity : Activity(), SensorEventListener {
	private lateinit var view: GameView

	private lateinit var sensorManager: SensorManager
	private var lightSensor: Sensor? = null
	private var accelerometerSensor: Sensor? = null

	private val sensorState = SensorState()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		// WindowInsetsControllerCompat(window, window.decorView)

		view = GameView(this, sensorState)
		setContentView(view)

		registerSensors()
	}

	override fun onSensorChanged(event: SensorEvent) {
		when (event.sensor.type) {
			Sensor.TYPE_LIGHT ->
				sensorState.lastAmbientLight = event.values[0]

			Sensor.TYPE_ACCELEROMETER ->
				sensorState.currentAccelerometer = Triple(
					event.values[0],
					event.values[1],
					event.values[2],
				)

			else -> Unit
		}
	}

	override fun onResume() {
		super.onResume()
		lightSensor.register()
		accelerometerSensor.register()
		// view.start()
	}

	override fun onPause() {
		super.onPause()
		lightSensor.unregister()
		accelerometerSensor.unregister()
		// view.suspend()
	}

	private fun Sensor?.register() {
		this?.let {
			sensorManager.registerListener(
				this@GameActivity,
				it,
				SensorManager.SENSOR_DELAY_NORMAL
			)
		}
	}

	private fun Sensor?.unregister() {
		this?.let {
			sensorManager.unregisterListener(
				this@GameActivity,
				it,
			)
		}
	}

	private fun registerSensors() {
		sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
		lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
		accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
		// Ignored
	}
}
