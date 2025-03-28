/*!
 * Copyright (c) Anton Haehn, Cynthia Rey, Tails91, All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
