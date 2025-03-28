package m2sdl.challongeandreuhide.geometry

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

class Rect(val x: Float, val y: Float, val width: Float, val height: Float, val radius: Float? = null) :
	RectF(x, y, x + width, y + height) {
	fun draw(canvas: Canvas, color: Int) {
		if (radius != null) {
			canvas.drawRoundRect(this, radius, radius, Paint(Paint.ANTI_ALIAS_FLAG).also { it.color = color })
		} else {
			canvas.drawRect(this, Paint(Paint.ANTI_ALIAS_FLAG).also { it.color = color })
		}
	}

	fun inner(of: Float): Rect {
		return Rect(x + of, y + of, width - of * 2, height - of * 2, radius?.let { radius - of })
	}

	fun with(
		x: Float? = null,
		y: Float? = null,
		width: Float? = null,
		height: Float? = null,
		radius: Float? = null
	): Rect {
		return Rect(
			x ?: this.x,
			y ?: this.y,
			width ?: this.width,
			height ?: this.height,
			if (radius == 0f) null else radius ?: this.radius
		)
	}
}
