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
