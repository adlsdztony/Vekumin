package icu.hku.vekumin

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import icu.hku.vekumin.ui.theme.VekuminTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val data: Uri? = intent?.data

        setContent {
            VekuminTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Auth(
                        data = data ?: Uri.EMPTY,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PlatformSelection(modifier: Modifier = Modifier) {
    // TODO
}

@Composable
fun Auth(data: Uri, modifier: Modifier = Modifier) {
    // if data is empty, show platform selection
    if (data == Uri.EMPTY) {
        PlatformSelection(modifier = modifier)
        return
    }

    // if data is not empty, save data to shared preferences
    // and navigate to main activity

    // save token and state to shared preferences
    // TODO

    // navigate to main activity
    // TODO
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    VekuminTheme {
        Auth(Uri.EMPTY)
    }
}
