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

package m2sdl.challongeandreuhide.entities

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.geometry.Rect
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

abstract class AbstractMobEntity(
	position: Offset,
	@DrawableRes
	resource: Int,
	width: Int,
	initialHealth: Int,
	val strength: Int,
	val speed: Float,
) : AbstractEntity(
	position,
	resource,
	width,
	initialHealth,
) {
	fun tick(target: AbstractEntity) {
		val vectorTowardsTower = target.state.position - state.position

		val isNextToTower =
			abs(vectorTowardsTower.x) < target.width / 2f * 1.25f &&
				abs(vectorTowardsTower.y) < target.width / 2f * 1.5f

		if (isNextToTower) {
			target.takeDamage(strength.toFloat() / 50f)
			return
		}

		val squaredDistanceToTower = vectorTowardsTower.x.pow(2) + vectorTowardsTower.y.pow(2)
		val speedSquared = speed.pow(2)

		val coefficient = sqrt(speedSquared / squaredDistanceToTower)
		state.position += vectorTowardsTower * coefficient
	}

	override fun draw(context: Context, canvas: Canvas) {
		super.draw(context, canvas)

		val x = state.position.x
		val y = state.position.y

		val drawable = context.resources.getDrawable(resource, null)
		val height = drawable.intrinsicHeight * width / drawable.intrinsicWidth

		val healthContainer = Rect(x - 30f, y - (height / 2f), 60f, 5f)
		val healthBar = healthContainer.with(width = healthContainer.width * (state.health / initialHealth))

		healthContainer.draw(canvas, Color.rgb(128, 0, 0))
		healthBar.draw(canvas, Color.RED)
	}
}
