package icu.hku.vekumin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import icu.hku.vekumin.post.Platform
import icu.hku.vekumin.post.data.Secret
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
fun PlatformItem(platform: Platform, enabled: Boolean = false) {
    val context = LocalContext.current
    val backgroundColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    Card(
        onClick = {
            // go to auth website
            val url = platform.authUrl
            val intent = Intent(Intent.ACTION_VIEW, url)
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)  // 根据条件设置背景色
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = platform.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (enabled) "Enabled, click to re-auth" else "Click to switch to ${platform.name}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun PlatformSelection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val platform = Secret.load(context)?.platform

    Column(modifier = modifier) {
        Platform.entries.forEach {
            PlatformItem(it, it == platform)
        }
    }

}

@Composable
fun Auth(data: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // if data is empty, show platform selection
    if (data == Uri.EMPTY) {
        PlatformSelection(modifier = modifier)
        return
    }

    fun getPlatformFromUri(uri: Uri): Platform? {
        val platform = uri.getQueryParameter("platform")?: return null
        return try {
            Platform.valueOf(platform)
        } catch (e: IllegalArgumentException) {
            null
        }
    }


    val platformEnum = getPlatformFromUri(data)
    if (platformEnum != null) {
        val keys = data.queryParameterNames.associateWith { data.getQueryParameter(it) ?: "" }
        Secret.fromMap(platformEnum, keys).save(context = context)
        Toast.makeText(context, "Successfully saved secret", Toast.LENGTH_SHORT).show()
    }

    PlatformSelection(modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    VekuminTheme {
        Auth(Uri.EMPTY)
    }
}
