package m2sdl.challongeandreuhide.threads

import android.graphics.Canvas
import android.view.SurfaceHolder
import m2sdl.challongeandreuhide.views.GameView

class GameThread(
	private val surfaceHolder: SurfaceHolder,
	private val gameView: GameView
) : Thread() {
	var running = false

	override fun run() {
		while (running) {
			var canvas: Canvas? = null
			try {
				canvas = this.surfaceHolder.lockCanvas()
				synchronized(surfaceHolder) {
					this.gameView.update()
					this.gameView.draw(canvas)
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
	}
}
