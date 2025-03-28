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

package m2sdl.challongeandreuhide.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.geometry.Rect
import m2sdl.challongeandreuhide.states.GameState
import m2sdl.challongeandreuhide.states.SensorState
import m2sdl.challongeandreuhide.threads.GameThread
import m2sdl.challongeandreuhide.threads.SensorThread

class GameView(context: Context, val sensorState: SensorState) : SurfaceView(context), SurfaceHolder.Callback {
	private val gameState = GameState(Offset(0f, 0f))

	private val gameThread = GameThread(context, holder, this, sensorState, gameState)
	private val sensorThread = SensorThread(sensorState)

	constructor(context: Context) : this(context, SensorState())

	init {
		holder.addCallback(this)
	}

	override fun draw(canvas: Canvas) {
		super.draw(canvas)

		drawArena(canvas)

		gameState.tower.draw(context, canvas)
		gameState.mobs.forEach { it.draw(context, canvas) }

		drawAttack(canvas) // only on attack
		drawHud(canvas)
	}

	private fun drawArena(canvas: Canvas) {
		var transparentValue = 0f
		Rect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()).draw(canvas, Color.rgb(148, 255, 145))
		canvas.drawCircle(
			gameState.tower.state.position.x,
			gameState.tower.state.position.y,
			500f,
			Paint().apply { color = Color.rgb(255, 215, 180) })

		transparentValue = when {
			sensorState.effectiveLight < 0.25 -> 0.35f
			sensorState.effectiveLight < 0.5 -> 0.25f
			sensorState.effectiveLight < 0.75 -> 0.15f
			else -> 0f
		}
		Rect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat()).draw(
			canvas,
			Color.argb(transparentValue, 0f, 0f, 0f)
		)
	}

	private fun drawPower(canvas: Canvas) {
		canvas.drawCircle(
			canvas.width - 50f,
			50f,
			30f,
			Paint().apply { color = Color.rgb(195, 195, 195) })
		val now = System.currentTimeMillis()
		val timerValue = (now - gameThread.degatsSecousses.initCooldown) / 1000
		if (timerValue < 5) {
			canvas.drawText(
				timerValue.toString(),
				canvas.width - 56f,
				55f,
				Paint().apply { color = Color.BLACK; textSize = 20f })
		} else {
			canvas.drawText(
				"OK",
				canvas.width - 63f,
				55f,
				Paint().apply { color = Color.BLACK; textSize = 20f })
		}
	}

	private fun drawAttack(canvas: Canvas) {
		canvas.drawCircle(
			gameState.tower.state.position.x,
			gameState.tower.state.position.y,
			250f,
			Paint().apply { color = Color.argb(35, 255, 0, 0) })


	}

	private fun drawHud(canvas: Canvas) {
		val coef = gameState.tower.state.health / gameState.tower.initialHealth

		val container = Rect(15f, 15f, 300f, 40f, 20f)
		val background = container.inner(5f)
		val healthBar = background.with(width = if (background.width * coef > 0) background.width * coef else 0f)

		container.draw(canvas, Color.rgb(128, 128, 128))
		background.draw(canvas, Color.rgb(128, 0, 0))
		healthBar.draw(canvas, Color.RED)

		drawPower(canvas)
	}

	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		gameState.tower.state.position = Offset(width.toFloat() / 2, height.toFloat() / 2)
	}

	override fun surfaceCreated(holder: SurfaceHolder) {
		start()
	}

	override fun surfaceDestroyed(holder: SurfaceHolder) {
		var retry = true
		while (retry) {
			try {
				suspend()
				gameThread.join()
				sensorThread.join()
			} catch (e: InterruptedException) {
				e.printStackTrace()
			}
			retry = false
		}
	}

	fun start() {
		gameThread.start()
		sensorThread.start()
	}

	fun suspend() {
		gameThread.shutdown()
		sensorThread.shutdown()
	}
}
