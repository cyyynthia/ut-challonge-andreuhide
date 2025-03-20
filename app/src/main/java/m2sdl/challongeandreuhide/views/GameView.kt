package m2sdl.challongeandreuhide.views;

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import android.view.SurfaceView
import m2sdl.challongeandreuhide.MainActivity.Companion.SHARED_PREFS_NAME
import m2sdl.challongeandreuhide.threads.GameThread

class GameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
	private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)

	private val thread = GameThread(holder, this)

	private var x = 0

	private val y: Int
		get() = sharedPreferences.getInt("valeur_y", 0)

	init {
		holder.addCallback(this)
	}

	override fun draw(canvas: Canvas) {
		super.draw(canvas)
		canvas.drawColor(Color.WHITE)
		val paint = Paint()
		paint.setColor(Color.rgb(250, 0, 0))
		canvas.drawRect(x, y, x + y, 200, paint)
	}

	override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
		// TODO()
	}

	override fun surfaceCreated(holder: SurfaceHolder) {
		thread.running = true
		thread.start()
	}

	override fun surfaceDestroyed(holder: SurfaceHolder) {
		var retry = true
		while (retry) {
			try {
				thread.running = false
				thread.join()
			} catch (e: InterruptedException) {
				e.printStackTrace()
			}
			retry = false
		}
	}

	fun update() {
		x = (x + 1) % 300
	}

	private fun Canvas.drawRect(left: Int, top: Int, right: Int, bottom: Int, paint: Paint) {
		drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
	}
}
