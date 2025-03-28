package m2sdl.challongeandreuhide.threads

import m2sdl.challongeandreuhide.sensors.MicrophoneSensor
import m2sdl.challongeandreuhide.states.SensorState
import kotlin.math.abs

class SensorThread(private val sensorState: SensorState) : Thread() {
	private val microphoneSensor = MicrophoneSensor()
	private val secousseTrigger = 5
	private var running = false
	private var amplitude: Double = 0.0

	override fun run() {
		microphoneSensor.startRecording()
		while (running) {
			// Gestion du microphone
			microphoneSensor.capture()
			amplitude = microphoneSensor.getAmplitude()
			sensorState.noise = when {
				amplitude > 20_000.0 -> SensorState.SensorNoise.Screaming
				amplitude > 10_000.0 -> SensorState.SensorNoise.Yelling
				amplitude > 1_000.0 -> SensorState.SensorNoise.Talking
				else -> null
			}

			//Gestion de l'accéléromètre
			if (sensorState.lastAccelerometer.first != 0f) {
				val xDiff = abs(sensorState.lastAccelerometer.first - sensorState.currentAccelerometer.first)
				val yDiff = abs(sensorState.lastAccelerometer.second - sensorState.currentAccelerometer.second)
				val zDiff = abs(sensorState.lastAccelerometer.third - sensorState.currentAccelerometer.third)

				sensorState.shaking = (
					(xDiff > secousseTrigger && yDiff > secousseTrigger)
						|| (xDiff > secousseTrigger && zDiff > secousseTrigger)
						|| (yDiff > secousseTrigger && zDiff > secousseTrigger)
					)
			}
			sensorState.lastAccelerometer = sensorState.currentAccelerometer
		}
		microphoneSensor.stopRecording()
	}

	fun getAmplitude(): Double {
		return amplitude
	}

	override fun start() {
		running = true
		super.start()
	}

	fun shutdown() {
		running = false
	}
}
