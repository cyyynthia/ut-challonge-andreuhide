package m2sdl.challongeandreuhide.entities

import android.content.Context
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import m2sdl.challongeandreuhide.states.EntityState

abstract class AbstractEntity(
	position: Offset,
	@DrawableRes
	val resource: Int,
	val width: Int,
	val initialHealth: Int
) {
	val state = EntityState(
		position = position,
		health = initialHealth.toFloat(),
	)

	fun takeDamage(damage: Float) {
		state.health -= damage
	}

	open fun draw(context: Context, canvas: Canvas) {
		canvas.save()

		val drawable = context.resources.getDrawable(resource, null)
		val height = drawable.intrinsicHeight * width / drawable.intrinsicWidth
		drawable.setBounds(0, 0, width, height)

		val x = state.position.x - (width / 2)
		val y = state.position.y - (height / 2)
		canvas.translate(x, y)
		drawable.draw(canvas)

		canvas.restore()
	}
}
