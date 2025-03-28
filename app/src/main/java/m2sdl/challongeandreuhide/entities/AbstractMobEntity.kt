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
