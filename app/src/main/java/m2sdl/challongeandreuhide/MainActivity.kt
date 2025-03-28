package m2sdl.challongeandreuhide

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.Manifest as AndroidManifest


class MainActivity : ComponentActivity() {
	@OptIn(ExperimentalMaterial3Api::class)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()

		setContent {
			var token by remember { mutableStateOf(0) }
			var permissionGranted by remember { mutableStateOf<Boolean?>(null) }

			Scaffold(
				topBar = {
					CenterAlignedTopAppBar(
						title = { Text("MineTower") }
					)
				}
			) { innerPadding ->
				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(innerPadding),
					verticalArrangement = Arrangement.Center,
					horizontalAlignment = Alignment.CenterHorizontally,
				) {
					RequestLocation(token) { permissionGranted = it }

					when (permissionGranted) {
						true -> {
							Button(
								onClick = {
									val intent = Intent(this@MainActivity, GameActivity::class.java)
									startActivity(intent)
								},
								modifier = Modifier
									.size(128.dp)
									.clip(RoundedCornerShape(50))
							) {
								Text("Jouer", style = MaterialTheme.typography.titleLarge)
							}
						}

						false -> {
							Button(onClick = { token++ }) {
								Text("Donne les perm :(")
							}
						}

						else -> Unit
					}

				}
			}
		}
	}
}

@Composable
fun RequestLocation(token: Any?, onPermissionResult: (Boolean) -> Unit) {
	val context = LocalContext.current

	val contract = ActivityResultContracts.RequestPermission()
	val permissionLauncher = rememberLauncherForActivityResult(contract, onPermissionResult)

	LaunchedEffect(token) {
		if (checkPermissions(context, AndroidManifest.permission.RECORD_AUDIO)) {
			onPermissionResult(true)
		} else {
			permissionLauncher.launch(AndroidManifest.permission.RECORD_AUDIO)
		}
	}
}

fun checkPermissions(ctx: Context, vararg permissions: String): Boolean {
	return permissions.any { ContextCompat.checkSelfPermission(ctx, it) == PackageManager.PERMISSION_GRANTED }
}
