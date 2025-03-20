package m2sdl.challongeandreuhide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import m2sdl.challongeandreuhide.ui.theme.ChallongeAndreuhideTheme
import m2sdl.challongeandreuhide.views.GameView


class MainActivity : ComponentActivity() {
	companion object {
		const val SHARED_PREFS_NAME: String = "appdata"
	}
	override fun onCreate(savedInstanceState: Bundle?) {
		val sharedPref = this.getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
		val baseValeurY = sharedPref.getInt("valeur_y", 0)
		val valeurY = (baseValeurY + 100) % 400
		val editor = sharedPref.edit()
		editor.putInt("valeur_y", valeurY)
		editor.apply()

		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContentView(GameView(this))
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	Text(
		text = "Hello $name!",
		modifier = modifier
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	ChallongeAndreuhideTheme {
		Greeting("Android")
	}
}
