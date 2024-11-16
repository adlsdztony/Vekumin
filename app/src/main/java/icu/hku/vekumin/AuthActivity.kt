package icu.hku.vekumin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import icu.hku.vekumin.post.Platform
import icu.hku.vekumin.post.data.Secret
import icu.hku.vekumin.ui.theme.VekuminTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import icu.hku.vekumin.post.data.PostConfig
import icu.hku.vekumin.quiz.data.QuizConfig

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val data: Uri? = intent?.data

        setContent {
            VekuminTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { AuthBar() }) { innerPadding ->
                    Auth(
                        data = data ?: Uri.EMPTY, modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthBar() {
    TopAppBar(title = { Text("Settings") })
}

@Composable
fun PlatformItem(platform: Platform, currentPlatform: Platform?, onDisable: () -> Unit) {
    val context = LocalContext.current
    val backgroundColor =
        if (currentPlatform == platform) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
    Card(
        onClick = {
            if (currentPlatform == platform) {
                onDisable()
            } else {
                // go to auth website
                val url = platform.authUrl
                val intent = Intent(Intent.ACTION_VIEW, url)
                context.startActivity(intent)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)  // 根据条件设置背景色
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = platform.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (currentPlatform == platform) "Enabled, click to delete" else "Click to switch to ${platform.name}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun PlatformSelection(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var platform by remember { mutableStateOf<Platform?>(Secret.load(context)?.platform) }
    LazyColumn(modifier = modifier) {
        items(Platform.entries.size) { index ->
            val platformEnum = Platform.entries[index]
            PlatformItem(platformEnum, platform, onDisable = {
                Secret.clear(context)
                Toast.makeText(context, "Successfully delete secret", Toast.LENGTH_SHORT).show()
                platform = null
            })
        }
    }
}

@Composable
fun SettingItem(title: String, contant: String?, onEdit: () -> Unit) {
    Card(
        onClick = {
            onEdit()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (contant != null) {
                Text(text = contant, style = MaterialTheme.typography.bodySmall)
            }
        }
}
    }

@Composable
fun QuizSettings(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val quizConfig = QuizConfig.load(context)
    Column(modifier = modifier) {
        SettingItem("amount: ${quizConfig?.get("amount")}", "Click to edit", onEdit = {
            // TODO: edit amount
        })
        SettingItem("difficulty: ${quizConfig?.get("difficulty")}", "Click to edit", onEdit = {
            // TODO: edit difficulty
        })
        SettingItem("health: ${quizConfig?.get("health")}", "Click to edit", onEdit = {
            // TODO: edit health
        })
    }
}

@Composable
fun PostSetting(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val postConfig = PostConfig.load(context)
    Column(modifier = modifier) {
        SettingItem("Post title", "Click to edit", onEdit = {
            // TODO: edit title
        })
        SettingItem("Post content", "Click to edit", onEdit = {
            // TODO: edit content
        })
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp, 0.dp)
        )
        content()
    }
}

@Composable
fun SettingPage(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Section(title = "Platform") {
            PlatformSelection()
        }
        Spacer(modifier = Modifier.padding(0.dp, 8.dp))
        Section(title = "Post") {
            PostSetting()
        }
        Spacer(modifier = Modifier.padding(0.dp, 8.dp))
        Section(title = "Quiz") {
            QuizSettings()
        }
    }
}

@Composable
fun Auth(data: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // if data is empty, show platform selection
    if (data == Uri.EMPTY) {
        SettingPage(modifier = modifier)
        return
    }

    fun getPlatformFromUri(uri: Uri): Platform? {
        val platform = uri.getQueryParameter("platform") ?: return null
        return try {
            Platform.valueOf(platform)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    val platformEnum = getPlatformFromUri(data)
    platformEnum?.let {
        val keys =
            data.queryParameterNames.associateWith { key -> data.getQueryParameter(key).orEmpty() }
        Secret.fromMap(it, keys).save(context)
        Toast.makeText(context, "Successfully saved secret", Toast.LENGTH_SHORT).show()
    } ?: run {
        Toast.makeText(context, "Failed to save secret", Toast.LENGTH_SHORT).show()
    }
    SettingPage(modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    VekuminTheme {
        Auth(Uri.EMPTY)
    }
}
