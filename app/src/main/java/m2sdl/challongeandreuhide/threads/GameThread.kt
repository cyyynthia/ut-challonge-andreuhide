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

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.EndActivity
import m2sdl.challongeandreuhide.attacks.ShakingAttack
import m2sdl.challongeandreuhide.distanceTo
import m2sdl.challongeandreuhide.entities.EntityRegistry
import m2sdl.challongeandreuhide.states.GameState
import m2sdl.challongeandreuhide.states.SensorState
import m2sdl.challongeandreuhide.views.GameView
import java.util.*
import kotlin.reflect.full.primaryConstructor

class GameThread(
	private val context: Context,
	private val surfaceHolder: SurfaceHolder,
	private val view: GameView,
	private val sensorState: SensorState,
	private val gameState: GameState,
) : Thread() {
	companion object {
		const val TICKS_PER_SECOND = 20
		const val RENDER_PER_SECOND = 60
	}

	private var running = false

	private val rng = Random()

	var degatsSecousses: ShakingAttack = ShakingAttack()

	private var initTimer: Long = 0

	private var tickCount: Long = 0

	override fun run() {
		val intervalBetweenTicks = 1000 / TICKS_PER_SECOND
		val intervalBetweenRenders = 1000 / RENDER_PER_SECOND

		var previousTickTime = 0L
		var previousRenderTime = 0L

		while (running) {
			val iterationTime = System.currentTimeMillis()
			val tickDelta = iterationTime - previousTickTime
			val renderDelta = iterationTime - previousRenderTime

			if (tickDelta >= intervalBetweenTicks) {
				previousTickTime = iterationTime
				previousRenderTime = iterationTime
				tick()
				draw()
			} else if (renderDelta >= intervalBetweenRenders) {
				previousRenderTime = iterationTime
				draw()
			}
		}
	}

	override fun start() {
		running = true
		super.start()
		initTimer = System.currentTimeMillis()
	}

	fun shutdown() {
		running = false
	}

	private fun tick() {
		tickCount++

		tickLiveness()
		tickMobSpawning()

		if (sensorState.shaking) {
			var dommages = degatsSecousses.fire()
			Log.i("Dommages", dommages.toString())
			gameState.mobs.forEach { it.takeDamage(dommages) }
		}

		val sensorNoise = sensorState.noise
		if (sensorNoise != null) {
			gameState.mobs.forEach {
				println(it.state.position.distanceTo(gameState.tower.state.position))
				if (it.state.position.distanceTo(gameState.tower.state.position) < 250f) {
					when (sensorNoise) {
						SensorState.SensorNoise.Talking -> it.takeDamage(1f)
						SensorState.SensorNoise.Yelling -> it.takeDamage(3f)
						SensorState.SensorNoise.Screaming -> it.takeDamage(5f)
					}
				}
			}
		}

		gameState.mobs.forEach { it.tick(gameState.tower) }
	}

	private fun tickMobSpawning() {
		if (gameState.mobs.size >= 50f) return
		if (tickCount % 10f != 0f) return
		if (!rng.nextBoolean()) return

		val mobsCount = rng.nextInt(5) + 1

		val maxHeight = this.surfaceHolder.surfaceFrame.width()
		val maxWidth = this.surfaceHolder.surfaceFrame.height()

		val offset = 150f
		val screenRegion = rng.nextInt(4)
		val position = when (screenRegion) {
			0 -> Offset(rng.nextFloatCompat(-offset, maxWidth + offset), -offset)
			1 -> Offset(maxHeight + offset, rng.nextFloatCompat(-offset, maxHeight + offset))
			2 -> Offset(rng.nextFloatCompat(-offset, maxWidth + offset), maxWidth + offset)
			3 -> Offset(-offset, rng.nextFloatCompat(-offset, maxHeight + offset))
			else -> throw IllegalStateException()
		}

		val entities =
			if (sensorState.effectiveLight < 0.5) EntityRegistry.NighttimeEntities
			else EntityRegistry.DayLightEntities

		repeat(mobsCount) {
			val offset = Offset(rng.nextFloatCompat(-50f, 50f), rng.nextFloatCompat(-50f, 50f))
			val entity = entities.random()
				.primaryConstructor!!
				.call(position + offset)

			gameState.mobs.add(entity)
		}
	}

	private fun tickMobActivity() {

	}

	private fun tickLiveness() {
		if (!gameState.tower.state.isAlive) {
			running = false
			val intent = Intent(context, EndActivity::class.java)
			Log.i("Temps", (System.currentTimeMillis() - initTimer).toString())
			intent.putExtra("tempsPris", System.currentTimeMillis() - initTimer)
			context.startActivity(intent)
		}

		gameState.mobs.removeIf { !it.state.isAlive }
	}

	private fun draw() {
		var canvas: Canvas? = null

		try {
			canvas = this.surfaceHolder.lockCanvas()
			synchronized(surfaceHolder) {
				this.view.draw(canvas)
			}
		} finally {
			canvas?.let {
				try {
					surfaceHolder.unlockCanvasAndPost(canvas)
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}
		}
	}

	private fun Random.nextFloatCompat(min: Float, max: Float): Float {
		return nextFloat() * (max - min) + min
	}
}
